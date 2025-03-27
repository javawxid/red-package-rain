package com.example.redpackagerain.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class ConcurrentHashMapExample {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个ConcurrentHashMap
        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        // 模拟多个线程同时写入数据
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executorService.submit(() -> {
                // 使用CAS操作进行数据插入
                concurrentMap.compute("key" + finalI, (key, value) -> (value == null) ? 1 : value + 1);
            });
        }
        // 关闭线程池并等待所有任务完成
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        // 打印结果
        concurrentMap.forEach((key, value) -> System.out.println(key + " -> " + value));
        // 模拟读取操作
        String keyToRead = "key5";
        Integer value = concurrentMap.get(keyToRead);
        System.out.println("Value for " + keyToRead + ": " + value);
        // 模拟删除操作
        String keyToDelete = "key3";
        concurrentMap.remove(keyToDelete);
        System.out.println("Removed " + keyToDelete + ", new map: " + concurrentMap);
    }
}