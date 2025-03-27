package com.example.redpackagerain.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
public class SemaphoreDemo {
    private static final Logger log = LoggerFactory.getLogger(SemaphoreDemo.class);
    // 同一时刻最多只允许有两个并发
    private static Semaphore semaphore = new Semaphore(2);
    private static Executor executor = Executors.newFixedThreadPool(10);
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> getProductInfo());
        }
    }
    public static String getProductInfo() {
        try {
            semaphore.acquire();
            log.info("请求服务");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            semaphore.release();
        }
        return "返回商品详情信息";
    }
}