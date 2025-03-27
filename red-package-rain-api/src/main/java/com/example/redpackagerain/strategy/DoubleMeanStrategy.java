package com.example.redpackagerain.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 二倍均值法：随机金额的平均值都是相等的，不会因为抢红包的先后顺序造成不公平。
 */
public class DoubleMeanStrategy implements RedPacketStrategy{

    @Override
    public List<BigDecimal> spilt(int totalAmount, int totalPeopleNum) {
        return GrabRedEnvelope(new BigDecimal(totalAmount), new BigDecimal("0.01"), new BigDecimal(totalPeopleNum));
    }


    public static void main(String[] args) {
        long startTime=System.currentTimeMillis();
        //初始化测试场景，模拟四种情况：
        //10人抢0.1元红包
        //10人抢1元红包
        //10人抢10元红包
        //10人抢100元红包
        BigDecimal[][] scene = {
                {new BigDecimal("0.1"), new BigDecimal("10")},
                {new BigDecimal("1"), new BigDecimal("10")},
                {new BigDecimal("10"), new BigDecimal("10")},
                {new BigDecimal("100"), new BigDecimal("10")}
        };
        //设置每个红包的最低金额为0.01元
        BigDecimal min = new BigDecimal("0.01");
        //分别测试各个红包
        for (BigDecimal[] decimals : scene) {
            final BigDecimal amount = decimals[0];
            final BigDecimal num = decimals[1];
            System.out.println("=====" + num + "个人抢一个"+ amount + "元的红包" + "=====");
            GrabRedEnvelope(amount, min, num);
        }
        long endTime=System.currentTimeMillis();
        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
    }

    //模拟抢红包过程
    private static List<BigDecimal> GrabRedEnvelope(BigDecimal amount, BigDecimal min, BigDecimal num){
        BigDecimal remain = amount.subtract(min.multiply(num));
        final Random random = new Random();
        final BigDecimal hundred = new BigDecimal("100");
        final BigDecimal two = new BigDecimal("2");
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal redpeck;
        List<BigDecimal> list = new ArrayList<>();
        for (int i = 0; i < num.intValue(); i++) {
            final int nextInt = random.nextInt(100);
            if(i == num.intValue() -1){
                redpeck = remain;
            }else{
                //RoundingMode.CEILING：取右边最近的整数
                //RoundingMode.FLOOR：取左边最近的正数
                redpeck = new BigDecimal(nextInt).multiply(remain.multiply(two).divide(num.subtract(new BigDecimal(i)),2, RoundingMode.CEILING)).divide(hundred,2, RoundingMode.FLOOR);
            }
            if(remain.compareTo(redpeck) > 0){
                remain = remain.subtract(redpeck);
            }else{
                remain = BigDecimal.ZERO;
            }
            sum = sum.add(min.add(redpeck));
            BigDecimal add = min.add(redpeck);
            System.out.println("第"+(i+1)+"个人抢到红包金额为："+add);
            list.add(add);
        }
        System.out.println("所有红包累计金额是否等于红包总金额："+compare(amount, sum));
        return list;
    }

    private static boolean compare(BigDecimal a, BigDecimal b){
        if(a.compareTo(b) == 0){
            return true;
        }
        return false;
    }
}
