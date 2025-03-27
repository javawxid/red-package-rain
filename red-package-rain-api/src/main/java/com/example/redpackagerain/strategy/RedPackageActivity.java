package com.example.redpackagerain.strategy;

import com.alibaba.fastjson.JSON;
import com.example.redpackagerain.constant.RedPackageRainConstant;
import com.example.redpackagerain.model.RedPackageActivityVo;
import com.example.redpackagerain.model.RedPackageVo;
import com.example.redpackagerain.spi.RedPackageKeyGenerator;
import com.example.redpackagerain.util.RedisUtil;
import com.example.redpackagerain.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
@Component
public class RedPackageActivity implements RedPackageActivityStrategy {


    /**
     * 解决办法：
     * 1.后端维护一个红包ID列表，当客户端请求抢红包时，后端检查当前红包ID是否还有红包可抢。如果没有，则提供下一个红包ID。
     * 2.客户端在抢完一个红包ID的红包后，主动请求后端获取下一个红包ID。
     * 3.使用WebSocket建立实时通信，当客户端抢完一个红包ID的红包时，后端通过WebSocket实时通知客户端下一个红包ID。
     * 4.客户端在初次连接时获取所有红包ID的列表，并在本地缓存。抢红包时按顺序使用这些ID。
     * 将红包id分批次拆分虽然解决了热点key和大key的问题，但是这也意味着一个批次的红包id是容易被抢完的，抢红包接口就需要重新调整代码，否则不适配。这里我就不继续编码了，偷个懒，哈哈，给大家提供一个思路
     * @param bigDecimals
     * @param req
     * @param delayTime
     * @param activityId
     */
    @Override
    public void saveRedPackageActivity(RedisUtil redisUtil,BigDecimal[] bigDecimals, RedPackageActivityVo req,long delayTime,String activityId,String filePath) {
        // 进行分组，保证每个红包id存储一定数量的红包金额，避免成为热key，将大 Key 拆分为多个小 Key，分散存储和访问压力。
        List<HashMap<String,BigDecimal[]>> split = split(bigDecimals);
        // 直接使用for循环而不是foreach，以减少不必要的迭代器创建
        for (int i = 0; i < split.size(); i++) {
            HashMap<String, BigDecimal[]> hashMap = split.get(i);
            // 获取Map的entrySet，避免在循环内部多次调用entrySet方法
            Set<Map.Entry<String, BigDecimal[]>> entries = hashMap.entrySet();
            // 遍历entrySet而不是使用迭代器，减少对象创建
            for (Map.Entry<String, BigDecimal[]> entry : entries) {
                String key = entry.getKey();
                BigDecimal[] value = entry.getValue();
                //这里提前保存到redis里，不要等定时任务执行的时候保存，因为拆分红包有一定计算量，拆分的越多耗时越长，提前保存减少耗时
                redisUtil.leftPushAllWithTTL(RedPackageRainConstant.RED_PACKAGE_REDPACKAGE_ID + key, value,req.getDuration() + 1000, TimeUnit.DAYS);
            }
        }
        // 红包集，使用Stream API收集所有map的key
        List<String> allKeys = split.stream() // 将List转换为Stream
                .flatMap(map -> map.keySet().stream()) // 将每个map的keySet转换为Stream，并合并成一个Stream
                .collect(Collectors.toList()); // 收集所有的key到一个Set中，自动去重
        String redPackageId = RedPackageKeyGenerator.getCurrentTimeCustomId();
        log.info("没拆分前的红包id:{},拆分后的红包id：{}" ,redPackageId, allKeys);
        //将拆分后的红包id映射关系保存入库
        redisUtil.setWithTTL(redPackageId,allKeys,3, TimeUnit.DAYS);
        // 启动定时任务，注：正式环境可改为rocketmq延迟消息
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            log.info("定时任务开始执行");
            // 构建前端红包雨活动数据
            RedPackageVo redPackgeVo = new RedPackageVo();
            redPackgeVo.setGenerationRate(req.getGenerationRate());
            redPackgeVo.setDuration(req.getDuration());
            redPackgeVo.setActivityId(activityId);
            redPackgeVo.setRedPackageId(redPackageId);
            redPackgeVo.setCreateTime(StringUtil.getCurrentTimeDate());
            //保存红包雨活动数据，后续会使用
            redisUtil.setWithTTL(RedPackageRainConstant.RED_PACKAGE_INFO_KEY + redPackgeVo.getActivityId(), redPackgeVo, req.getDuration() + 10000, TimeUnit.SECONDS);//调试模式下，缓存时间设置大一点
            // redis广播信息，服务器收到广播消息后，websocket推送消息给前端用户开启红包雨活动
            String redpackge = JSON.toJSONString(redPackgeVo);
            redisUtil.convertAndSend(RedPackageRainConstant.RED_PACKAGE_REDIS_QUEUE_MESSAGE_KEY, redpackge);
            log.info("红包雨活动广播：{}", redpackge);
            for (int i = 0; i < 5000; i++) {
                writeFile(filePath, "redPackgeId.txt", redPackageId + "\r\n"); // 将redPackgeId写入文件
            }
            log.info("定时任务执行结束");
        }, delayTime, TimeUnit.SECONDS);
        executor.shutdown();
    }

    /**
     * 由于将红包id进行了拆分，那么就必定会出现某一用户访问完某个红包池之后，发现红包池里面的红包被抢完了。后续需要做映射。
     * 例如100万个红包，以5000个为一个批次，需要拆分成200个，就有200个红包id，提供给前端的红包id就一个，映射后端红包id有200个。拿这200个去缓存红包池中去金额，如果200个红包id中有一个取完了之后，就将这个红包id从映射关系中去掉，同时换下一个红包id去。
     * 映射可以用键值对，key=还未拆分的红包id,value=拆分后的红包id集合。也可以使用队列或者其他数据结构
     * 需要注意的是高并发场景下将映射关系中没有红包金额的红包id移除存在并发问题，可以考虑使用原子操作类，或者其他方式
     * @param arr
     * @return
     */
    public List<HashMap<String,BigDecimal[]>> split(BigDecimal[] arr){
        int groupSize = 30; //每组的大小,可根据业务需求进行调整。
        int length = arr.length; // 红包金额数组的长度
        int remainder = length % groupSize; // 确定余数
        int groupNums = length / groupSize; // 计算组数
        BigDecimal[][] groups;
        if (remainder == 0) {
            groups = new BigDecimal[groupNums][groupSize]; // 如果余数为0，则创建groupNums组，每组有groupSize个元素的二维数组
        } else {
            groups = new BigDecimal[groupNums + 1][groupSize]; // 如果余数不为0，则创建(groupNums+1)组，每组有groupSize个元素的二维数组
        }
        int index = 0; //数组下标
        for (int i = 0; i < groupNums; i++) {
            for (int j = 0; j < groupSize; j++) {
                groups[i][j] = arr[index++]; // 将红包金额数组中的元素逐个分配到二维数组中
            }
        }
        if (remainder != 0) {
            for (int i = 0; i < remainder; i++) {
                groups[groupNums][i] = arr[index++]; // 将余数中的元素分配到二维数组中的最后一组
            }
        }
        List list = new ArrayList<HashMap<String,BigDecimal[]>>();
        for (int i = 0; i < groups.length; i++) {
            String redPackageId = RedPackageKeyGenerator.getCurrentTimeCustomId();
            BigDecimal[] bigDecimals = groups[i];
            Map<String,BigDecimal[]> map = new HashMap<>();
            map.put(redPackageId,bigDecimals);
            list.add(map);
        }
        return list;
    }

    public static void writeFile(String directoryName, String fileName, String content) {
        // 创建File对象，表示要写入的目录
        File directory = new File(directoryName);
        // 如果目录不存在，则创建该目录
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 创建File对象，表示要写入的文件
        File file = new File(directory, fileName);
        // 使用try-with-resources语句，自动关闭流
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
