package com.example.redpackagerain.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.example.redpackagerain.common.Result;
import com.example.redpackagerain.common.ResultCodeEnum;
import com.example.redpackagerain.constant.RedPackageRainConstant;
import com.example.redpackagerain.model.RedPackageActivityVo;
import com.example.redpackagerain.model.RedPackageRainVo;
import com.example.redpackagerain.model.RedPackageVo;
import com.example.redpackagerain.producer.MessageProducer;
import com.example.redpackagerain.strategy.RedPackageActivityStrategy;
import com.example.redpackagerain.strategy.RedPacketStrategy;
import com.example.redpackagerain.strategy.StrategyEnum;
import com.example.redpackagerain.strategy.StrategyFactory;
import com.example.redpackagerain.util.ConcurrentHashMapPool;
import com.example.redpackagerain.util.RedisUtil;
import com.example.redpackagerain.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedPackageRainServcie {

    @Autowired
    private ConcurrentHashMapPool pool;

    @Resource
    private MessageProducer messageProducer;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ThreadPooService threadPooService;

    @Value("${onOff}")
    private boolean onOff;

    @Value("${filepath}")
    private String filePath;

    /**
     * 保存红包雨日志
     * todo 使用线程池发送消息可能会出现在高并发场景下，生产者发送消息的并发量过高，超过了 RocketMQ 服务器处理能力的上限。RocketMQ 服务器处理消息的能力不足，可能是由于硬件资源限制或配置不当。解决方案：分批处理，这里我就不写了
     * 	import org.apache.rocketmq.common.message.Message;
     * 	import org.apache.rocketmq.spring.core.RocketMQTemplate;
     * 	import org.springframework.beans.factory.annotation.Autowired;
     * 	import org.springframework.stereotype.Service;
     * 	import java.util.ArrayList;
     * 	import java.util.List;
     *  @Service
     *  public class MessageSender {
     *     @Autowired
     *     private RocketMQTemplate rocketMQTemplate;
     * 	   public void sendBatchMessages(List<Message> messages, int batchSize) {
     * 	       for (int i = 0; i < messages.size(); i += batchSize) {
     * 	           List<Message> batch = messages.subList(i, Math.min(i + batchSize, messages.size()));
     * 	           rocketMQTemplate.syncSendBatch("topicName", batch);
     *           }
     *       }
     *    }
     * @param activityId
     * @param redPackageId
     * @param userId
     * @param partRedPackage
     */
    public void saveRedPackageInfo(String activityId, String redPackageId,String userId,String partRedPackage,String createTime){
        ConcurrentHashMap concurrentHashMap =  pool.acquire(); // 从对象池中获取一个ConcurrentHashMap实例
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_ACTIVITY_ID,activityId);
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_REDPACKAGE_ID,redPackageId);
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_USER_ID,userId);
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_VALUE,partRedPackage);
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_MESSAGE_ID,IdUtil.simpleUUID().replace("-", ""));
        concurrentHashMap.put(RedPackageRainConstant.CREATE_TIME,createTime);
        //将ConcurrentHashMap对象转换成字符串。
        String convertToString = StringUtil.convertToString(concurrentHashMap);
        //释放对象
        pool.release(concurrentHashMap);
        //发送MQ消息 发送字符串比发送对象的网络传输更小。这是因为字符串可以被序列化为字节数组，而对象需要被序列化为字节数组，并包含对象的类信息和其他序列化数据。因此，发送字符串可以节省网络传输的带宽。
        // todo 消息中间件可能会出现处理不过来的情况，异常如下： sendDefaultImpl call timeout; nested exception is org.apache.rocketmq.remoting.exception.RemotingTooMuchRequestException: sendDefaultImpl call timeout。需要调整rocketmq相关参数
        messageProducer.sendMessage(RedPackageRainConstant.RED_PACKAGE_RAIN_TOPIC,convertToString);
/*        // TODO 测试事务消息零丢失（已成功实现）
        messageProducer.sendMessageInTransaction(ThreadPoolConstant.TRANSACTION_TOPIC,convertToString,redPackageId,ThreadPoolConstant.TAG_THREADPOOL_PARAMETER);
        messageProducer.sendMessageInTransaction(ThreadPoolConstant.TRANSACTION_TOPIC,convertToString,redPackageId,ThreadPoolConstant.TAG_THREADPOOL_TASK);
        messageProducer.sendMessageInTransaction(ThreadPoolConstant.TRANSACTION_TOPIC,convertToString,redPackageId,"TagC");
        messageProducer.sendMessageInTransaction(ThreadPoolConstant.TRANSACTION_TOPIC,convertToString,redPackageId,"TagD");
        messageProducer.sendMessageInTransaction(ThreadPoolConstant.TRANSACTION_TOPIC,convertToString,redPackageId,"UNKNOWN");*/
    }


    /**
     * LinkedBlockingQueue是一个阻塞的队列,内部采用链表的结果,通过两个ReenTrantLock来保证线程安全
     * LinkedBlockingQueue与ArrayBlockingQueue的区别
     * ArrayBlockingQueue默认指定了长度,而LinkedBlockingQueue的默认长度是Integer.MAX_VALUE,也就是无界队列,在移除的速度小于添加的速度时，容易造成OOM。
     * ArrayBlockingQueue的存储容器是数组,而LinkedBlockingQueue是存储容器是链表
     * 两者的实现队列添加或移除的锁不一样，ArrayBlockingQueue实现的队列中的锁是没有分离的，即添加操作和移除操作采用的同一个ReenterLock锁，
     * 而LinkedBlockingQueue实现的队列中的锁是分离的，其添加采用的是putLock，移除采用的则是takeLock，这样能大大提高队列的吞吐量，
     * 也意味着在高并发的情况下生产者和消费者可以并行地操作队列中的数据，以此来提高整个队列的并发性能。
     */
    private final Queue<RedPackageRainVo> queue = new LinkedBlockingQueue();

    @PostConstruct // 标记为Spring框架初始化完成后自动调用的方法
    public void init() {
        // 创建一个支持定时任务的线程池，限定线程数目为1
        //定时任务线程池,创建一个支持定时、周期性或延时任务的限定线程数目(这里传入的是1)的线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        // 定时任务，每100毫秒后开始执行，之后每隔10毫秒执行一次
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                List<RedPackageRainVo> list = new ArrayList<>();
                // 获取队列中当前待处理的请求数量
                int size = queue.size();
                // 如果队列中没有请求，则直接返回，不执行后续操作
                //如果队列没数据,表示这段时间没有请求,直接返回
                if (size == 0) {
                    return;
                }
                // 记录日志，表示合并了指定数量的请求
                log.info("合并了 [" + size + "] 个请求");
                // 从队列中取出请求，并添加到list中，但最多只取1000个
                //将队列的请求消费到一个集合保存
                for (int i = 0; i < size; i++) {
                    // 限制每次批量的数量,超过最大任务数，等下次执行
                    if (i < 1000) {
                        list.add(queue.poll());
                    }
                }
                if (!CollectionUtils.isEmpty(list)) {
                    // 调用service处理请求，并获取处理结果
                    this.snatchRedPackageList(list);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                // 如果在等待过程中线程被中断，则捕获异常并打印堆栈信息
                e.printStackTrace();
            }
        }, 100, 1, TimeUnit.MILLISECONDS); // 初始化后100毫秒开始执行，之后每隔10毫秒执行一次
    }


    private void snatchRedPackageList(List<RedPackageRainVo> redPackageRainVoList) {
        // 使用groupingBy收集器按照userId分组
        Map<String, List<RedPackageRainVo>> groupedRequests = redPackageRainVoList.stream().collect(Collectors.groupingBy(RedPackageRainVo::getUserId));
        groupedRequests.forEach((userId, requestList) -> {
            List<String> redPackageIds = requestList.stream()
                    .map(RedPackageRainVo::getRedPackageId)
                    .map(id -> RedPackageRainConstant.RED_PACKAGE_REDPACKAGE_ID + id) // 在这里添加前缀
                    .collect(Collectors.toList());
            //同一个用户的红包id集合取出红包金额集，利用管道减少网络传输，提升吞吐量
            List<String> redPackage = redisUtil.leftPopBatch(redPackageIds);
            if (!CollectionUtils.isEmpty(redPackage)) {
                //批量插入
                redisUtil.setRedPackages(userId,redPackage);
                // 使用Stream API计算总和
                BigDecimal sum = redPackage.stream()
                        .map(s -> s.replace("\"", "")) // 去除逗号
                        .map(BigDecimal::new) // 将每个字符串转换为BigDecimal
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // 累加BigDecimal值
                //将业务层需要执行的方法放到线程池里，提交任务去执行
                try {
                    //自适应（根据当前机器的线程数适配核心线程数和最大线程数）全局线程池\
                    ThreadPoolExecutor executor = threadPooService.getThreadPoolExecutor();
                    executor.execute(() -> {
                        saveRedPackageInfoMergeRequest(redPackageRainVoList,sum);
                    });
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 合并请求的保存红包雨日志
     */
    public void saveRedPackageInfoMergeRequest(List<RedPackageRainVo> list,BigDecimal sum){
        ConcurrentHashMap concurrentHashMap =  pool.acquire(); // 从对象池中获取一个ConcurrentHashMap实例
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_LIST,list);
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_SUM,sum);
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_MESSAGE_ID,IdUtil.simpleUUID().replace("-", ""));
        //将ConcurrentHashMap对象转换成字符串。
        String convertToString = StringUtil.convertToString(concurrentHashMap);
        //释放对象
        pool.release(concurrentHashMap);
        //发送MQ消息 发送字符串比发送对象的网络传输更小。这是因为字符串可以被序列化为字节数组，而对象需要被序列化为字节数组，并包含对象的类信息和其他序列化数据。因此，发送字符串可以节省网络传输的带宽。
        messageProducer.sendMessage(RedPackageRainConstant.RED_PACKAGE_RAIN_MERGERE_QUEST_TOPIC,convertToString);
    }

    public boolean snatchRedPackageMergeRequest(RedPackageRainVo redPackage,String userId) {
        //当前用户不能抢其他用户的红包
        if (!redPackage.getUserId().equals(userId)) {
            return false;
        }
        // 将请求对象放入队列queue中
        return queue.offer(redPackage);
    }

    public Result snatchRedPackage(RedPackageRainVo redPackage,String userTokenId) throws Exception{
        log.info("=======snatchRedPackage:redPackage{},userTokenId:{}",redPackage.toString(),userTokenId);
        //红包唯一标识\红包的key
        String redPackageId = redPackage.getRedPackageId();
        //用户id\用户token
        String userId = redPackage.getUserId();
        if (!userTokenId.equals(userId)) {
            //当前用户不能抢其他用户的红包
            return Result.error(ResultCodeEnum.WRONG_USER_RED_PACKET.getCode(),ResultCodeEnum.WRONG_USER_RED_PACKET.getMessage());
        }
        //活动id
        String activityId = redPackage.getActivityId();
        //从缓存中获取拆分后的红包映射关系
        List<String> redPackageIds = redisUtil.redPackageIds(redPackageId);
        for (String packageId : redPackageIds) {
            //使用StringBuilder拼接字符串，作为红包的键（key）
            String redAppend = StringUtil.StringAppend(RedPackageRainConstant.RED_PACKAGE_REDPACKAGE_ID, packageId);
            //从redis缓存中获取红包池中的红包
            String partRedPackage = redisUtil.leftPop(redAppend);
            //判断是否为空，不为空进入后续流程；为空直接返回
            if (StringUtils.isNotEmpty(partRedPackage)) {
                //将红包的key和用户的id作为存储redis缓存已抢红包池的键（key）
                String redConsumeAppend = StringUtil.StringAppend(RedPackageRainConstant.RED_PACKAGE_CONSUME_KEY,  redPackageId, ":",userId);
                //存入redis缓存并且设置过期时间（使用了redis事务，保证原子性，因为操作简单、依赖关系简单，使用使用redis事务比使用 Lua 脚本更适合当前场景）
                Boolean result = redisUtil.multi(redConsumeAppend, partRedPackage, 36000);
                //同步操作执行成功后，使用定制的全局线程池提交任务，异步处理业务逻辑
                if (result) {
                    //将业务层需要执行的方法放到线程池里，提交任务去执行
                    Method method = this.getClass().getMethod("saveRedPackageInfo", String.class,String.class, String.class,String.class,String.class);
                    //使用setAccessible(Boolean.TRUE)允许访问该方法的私有或受保护访问权限
                    method.setAccessible(Boolean.TRUE);
                    //自适应（根据当前机器的线程数适配核心线程数和最大线程数）全局线程池
                    threadPooService.runMethod(this, method,activityId,redPackageId,userId,partRedPackage,StringUtil.getCurrentTimeDate());
                }
                return Result.build(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS);
            }else {
                //如果拆分后的红包id没有红包金额，则从红包映射关系中取下一个，同时将这个红包id从映射关系中移除。
                redisUtil.removeElementFromList(redPackageId,packageId,0);
                //跳出本次循环
                break;
            }
        }
        return Result.error(ResultCodeEnum.RED_PACKAGE_FINISHED.getCode(),ResultCodeEnum.RED_PACKAGE_FINISHED.getMessage());
    }


    public Result<RedPackageVo> addRedPackageActivity(RedPackageActivityVo req) {
        // 记录活动信息
        String activityId = IdUtil.simpleUUID().replace("-", "");
        //生成活动唯一标识，相当于活动的id标识（活动唯一）
        req.setActivityId(RedPackageRainConstant.RED_PACKAGE_ACTIVITY_ID + activityId);
        //保存活动，可以配置很多活动，互不影响
        redisUtil.sSet(RedPackageRainConstant.RED_PACKAGE_ACTIVITY_LIST,36000, req);
        // 活动开始后才初始化红包雨相关信息，保证所有用户同一时刻抢红包（公平、准点、公正）
        // 计算活动开始的剩余时间：单位秒
        LocalDateTime localDateTime = req.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long delayTime = ChronoUnit.SECONDS.between(LocalDateTime.now(),localDateTime);
        //活动开始，拆红包，将总金额totalMoney拆分为redPackageNumber个子红包
        RedPacketStrategy doubleMeanStrategy = StrategyFactory.getRedPacketStrategy(StrategyEnum.DoubleMeanStrategy);
        // 使用双倍均值法拆分红包，并返回一个包含红包金额的列表 拆分红包算法通过后获得的多个子红包数组
        List<BigDecimal> list = doubleMeanStrategy.spilt(req.getTotalMoney(), req.getRedPackageNumber());
        BigDecimal[] splitRedPackages = list.toArray(new BigDecimal[0]); // 将列表转换为数组
        log.info("拆红包: {}", JSON.toJSONString(splitRedPackages));
        //使用模板方法模式执行不同的代码
        RedPackageActivityStrategy redPackageActivityStrategy;
        if (onOff) {
            redPackageActivityStrategy = StrategyFactory.getRedPackageActivityStrategy(StrategyEnum.RedPackageActivity);
        }else {
            redPackageActivityStrategy = StrategyFactory.getRedPackageActivityStrategy(StrategyEnum.RedPackageActivityMergeRequest);
        }
        redPackageActivityStrategy.saveRedPackageActivity(redisUtil,splitRedPackages, req,delayTime, activityId,filePath);
        return Result.build(activityId, ResultCodeEnum.SUCCESS);
    }

    public Result<List<RedPackageActivityVo>> listRedPackage() {
        Set<RedPackageActivityVo> redPackgeActivityVoSet = redisUtil.membersRedPackgeActivity(RedPackageRainConstant.RED_PACKAGE_ACTIVITY_LIST);
        if (!CollectionUtils.isEmpty(redPackgeActivityVoSet)) {
            List<RedPackageActivityVo> redPackgeActivityVoList = new ArrayList<>(redPackgeActivityVoSet);
            //排序
            Collections.sort(redPackgeActivityVoList, new Comparator<RedPackageActivityVo>() {
                @Override
                public int compare(RedPackageActivityVo p1, RedPackageActivityVo p2) {
                    return p2.getDate().compareTo(p1.getDate());
                }
            });
            return Result.build(redPackgeActivityVoList, ResultCodeEnum.SUCCESS);
        }
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    //有请求合并的用户已抢红包金额
    public Result<Integer> recordMerage(String userId) {
        List<String> bigDecimalStrings = redisUtil.getRedPackagesAsStringList(userId);
        // 将字符串列表转换为BigDecimal列表
        List<BigDecimal> bigDecimals = bigDecimalStrings.stream()
                .map(BigDecimal::new) // 使用BigDecimal的构造函数将字符串转换为BigDecimal
                .collect(Collectors.toList());
        // 计算BigDecimal列表的总和
        BigDecimal sum = bigDecimals.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 使用reduce方法和add方法累加所有BigDecimal值
        log.info("userId:{},Total amount: {}" ,userId, sum);
        return Result.build(sum,ResultCodeEnum.SUCCESS);
    }

    //没有请求合并的用户已抢红包金额
    public Result<Integer> record(String redPackageId, String userId) {
        //获取当前用户抢过的红包列表
        long size = redisUtil.listLength(RedPackageRainConstant.RED_PACKAGE_CONSUME_KEY + redPackageId + ":" + userId);
        List<Integer> list = redisUtil.range(RedPackageRainConstant.RED_PACKAGE_CONSUME_KEY + redPackageId + ":" + userId, 0, size - 1);
        //当前用户红包总金额
        Integer total = list.stream().reduce(0, Integer::sum);
        return Result.build(total, ResultCodeEnum.SUCCESS);
    }
}
