package com.example.redpackagerain.strategy;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.example.redpackagerain.constant.RedPackageRainConstant;
import com.example.redpackagerain.model.RedPackageActivityVo;
import com.example.redpackagerain.model.RedPackageVo;
import com.example.redpackagerain.util.RedisUtil;
import com.example.redpackagerain.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Slf4j
@Component
public class RedPackageActivityMergeRequest implements RedPackageActivityStrategy {
    @Autowired
    private RedisUtil redisUtil;

    /**
     * todo 存在 热点数据倾斜问题 大key需要拆分 ,这个可以在RedPackageActivity中解决，对红包id进行拆分，将一个红包id保存所有红包金额拆分成多个红包id，一个红包id保存一批红包
     * @param bigDecimals
     * @param req
     * @param delayTime
     * @param activityId
     */
    @Override
    public void saveRedPackageActivity(RedisUtil redisUtil,BigDecimal[] bigDecimals, RedPackageActivityVo req,long delayTime,String activityId,String filePath) {
        String redPackageId = IdUtil.simpleUUID().replace("-", "");
        // 红包并保存进list结构里面且设置过期时间,红包雨的持续时间为过期时间，由于页面有倒计时，需要把倒计时时间加上
        redisUtil.leftPushAllWithTTL(RedPackageRainConstant.RED_PACKAGE_REDPACKAGE_ID + redPackageId, bigDecimals,req.getDuration() + 1000, TimeUnit.MILLISECONDS);
        // 启动定时任务，注：正式环境可改为rocketmq延迟消息
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            // 构建前端红包雨活动数据
            RedPackageVo redPackgeVo = new RedPackageVo();
            redPackgeVo.setGenerationRate(req.getGenerationRate());
            redPackgeVo.setDuration(req.getDuration());
            redPackgeVo.setActivityId(activityId);
            redPackgeVo.setRedPackageId(redPackageId);
            redPackgeVo.setCreateTime(StringUtil.getCurrentTimeDate());
            //保存红包雨活动数据，后续会使用
            redisUtil.setWithTTL(RedPackageRainConstant.RED_PACKAGE_INFO_KEY + redPackgeVo.getActivityId(), redPackgeVo, req.getDuration() + 10000, TimeUnit.MILLISECONDS);
            // redis广播信息，服务器收到广播消息后，websocket推送消息给前端用户开启红包雨活动
            String redpackge = JSON.toJSONString(redPackgeVo);
            redisUtil.convertAndSend(RedPackageRainConstant.RED_PACKAGE_REDIS_QUEUE_MESSAGE_KEY, redpackge);
            log.info("红包雨活动广播：{}", redpackge);
        }, delayTime, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
