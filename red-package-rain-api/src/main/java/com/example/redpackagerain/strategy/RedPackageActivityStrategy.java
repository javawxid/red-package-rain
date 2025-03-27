package com.example.redpackagerain.strategy;

import com.example.redpackagerain.model.RedPackageActivityVo;
import com.example.redpackagerain.util.RedisUtil;

import java.math.BigDecimal;

/**
 * @Description 策略工厂
 */
public interface RedPackageActivityStrategy {

    void saveRedPackageActivity(RedisUtil redisUtil,BigDecimal[] bigDecimals, RedPackageActivityVo req, long delayTime, String activityId,String filePath);

}
