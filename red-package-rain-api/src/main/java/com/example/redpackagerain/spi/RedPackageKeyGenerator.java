package com.example.redpackagerain.spi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RedPackageKeyGenerator {

    static AtomicLong atom = new AtomicLong(0);
    public static String getCurrentTimeCustomId(){
        LocalDate now = LocalDate.now(); // 获取当前日期
        String format = DateTimeFormatter.ofPattern("yyyyM").format(now); // 格式化为年月，不包含月份前导0
        String sixDigits = new Random().ints(0, 10)
                .limit(6)
                .mapToObj(i -> String.valueOf(i))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return format + atom.incrementAndGet() + sixDigits;
    }

    public static void main(String[] args) {
        LocalDate now = LocalDate.now(); // 获取当前日期
        String format = DateTimeFormatter.ofPattern("yyyyM").format(now); // 格式化为年月，不包含月份前导0
        System.out.println(format);
    }

}
