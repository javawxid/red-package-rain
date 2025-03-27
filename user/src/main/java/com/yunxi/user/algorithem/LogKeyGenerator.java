package com.yunxi.user.algorithem;

import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public class LogKeyGenerator implements ShardingKeyGenerator {

    private AtomicLong atom = new AtomicLong(0);

    @Override
    public Comparable<?> generateKey() {
        LocalDateTime now = LocalDateTime.now();
        String format = DateTimeFormatter.ofPattern("HHmmssSSS").format(now);
        return Long.parseLong(format + atom.incrementAndGet());
    }

    public static void main(String[] args) {
        AtomicLong atom = new AtomicLong(0);
        LocalDateTime now = LocalDateTime.now();
        String format = DateTimeFormatter.ofPattern("HHmmssSSS").format(now);
        System.out.println(Long.parseLong(format + atom.incrementAndGet()));
    }

    @Override
    public String getType() {
            return "LOGKEY";
    }

    @Override
    public Properties getProperties() {
        return new Properties();
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
