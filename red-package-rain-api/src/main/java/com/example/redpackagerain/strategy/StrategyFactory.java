package com.example.redpackagerain.strategy;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 策略工厂、享元模式/单例模式
 */
@Slf4j
public class StrategyFactory {

    private static final Map<String, RedPacketStrategy> redPacketStrategyMap = new ConcurrentHashMap();
    private static final Map<String, RedPackageActivityStrategy> redPackageActivityStrategyMap = new ConcurrentHashMap();

    public static RedPacketStrategy getRedPacketStrategy(StrategyEnum strategyEnum){
        RedPacketStrategy redPacketStrategy = redPacketStrategyMap.get(strategyEnum.getValue());
        if (redPacketStrategy == null) {
            try {
                redPacketStrategy = (RedPacketStrategy) Class.forName(strategyEnum.getValue()).newInstance();
                redPacketStrategyMap.put(strategyEnum.getValue(), redPacketStrategy);
            } catch (Exception e) {
                //异常信息打印
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return redPacketStrategy;
    }

    public static RedPackageActivityStrategy getRedPackageActivityStrategy(StrategyEnum strategyEnum){
        RedPackageActivityStrategy redPackageActivityStrategy = redPackageActivityStrategyMap.get(strategyEnum.getValue());
        if (redPackageActivityStrategy == null) {
            try {
                redPackageActivityStrategy = (RedPackageActivityStrategy) Class.forName(strategyEnum.getValue()).newInstance();
                redPackageActivityStrategyMap.put(strategyEnum.getValue(), redPackageActivityStrategy);
            } catch (Exception e) {
                //异常信息打印
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return redPackageActivityStrategy;
    }
}
