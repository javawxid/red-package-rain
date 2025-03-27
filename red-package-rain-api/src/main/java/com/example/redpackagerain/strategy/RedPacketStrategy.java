package com.example.redpackagerain.strategy;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 策略工厂
 */
public interface RedPacketStrategy {

    List<BigDecimal> spilt(int totalAmount, int totalPeopleNum);

}
