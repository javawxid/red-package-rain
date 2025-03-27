package com.example.redpackagerain.strategy;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 线段切割法:把红包总金额想象成一条很长的线段，每个人抢到的金额是主线段拆分出来的若干个子线段。
 */
public class SegmentCuttingStrategy implements RedPacketStrategy{

    @Override
    public List<BigDecimal> spilt(int totalAmount, int totalPeopleNum) {
        return exe(totalAmount, totalPeopleNum);
    }

    /**
     * 微信红包首先将金额M元按照要发的人数N分成随机的N个金额不等的红包份额。生成N个份之后，用户请求到了就将1份红包发个用户。
     * 生成红包金额数的算法采用线段切割法：将金额看成一条线段，线段的长度范围是0到M*100，首先需要生成1~（M*100-1） 中间的(N-1)个随机的且不重复的数，可以使用这(N-1)个数去切割线段，切割后的每一份就是红包的金额数。
     * @param amount 红包金额
     * @param friend 发出的红包数
     */
    private static List<BigDecimal> exe(int amount,int friend){
        int seed = 100 * amount;//将金额数按照一分钱来等分100*amount分
        int length  = 0;
        int m_length = friend-1;//在0~seed中生成m_length个随机不重复的数字

        //利用SecureRandom生成随机数
        SecureRandom secureRandom = null;
        //保证不重复且升序排序
        SortedSet<Integer> set = new TreeSet<>();
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        while (length < m_length){
            int num = secureRandom.nextInt(seed);
            if(num == 0) num = 1;
            set.add(num);
            length = set.size();
        }

        int preNum = 0;
        int currentNum = 0;
        int calc = 0;
        List<BigDecimal> list = new ArrayList<>();
        for (int i = 0; i < set.toArray().length+1 ; i++) {
            if (i < set.toArray().length){
                currentNum = (int)set.toArray()[i];
            }else {
                currentNum = seed;
            }
            System.out.println("第" +(i+1) +"个红包");
            BigDecimal bigDecimal = new BigDecimal((currentNum - preNum)).divide(new BigDecimal(100));
            System.out.println("红包金额=" + bigDecimal + "元");
            list.add(bigDecimal);
            calc += (currentNum - preNum);
            System.out.println("已发:" +new BigDecimal(calc).divide(new BigDecimal(100)) + "元");
            preNum = currentNum;
        }
        return list;
    }
}
