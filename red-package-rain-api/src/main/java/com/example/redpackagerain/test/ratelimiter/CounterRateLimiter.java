package com.example.redpackagerain.test.ratelimiter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;
// 定义一个基于计数器的速率限制器类
public class CounterRateLimiter {
    // 每个窗口允许的最大请求数
    private final int limit;
    // 窗口大小（毫秒）
    private final long windowSizeInMs;
    // 当前窗口内的请求数计数器
    private final AtomicInteger count;
    // 上次重置计数器的时间
    private long lastResetTime;
    // 构造函数，初始化速率限制器
    public CounterRateLimiter(int limit, long windowSizeInSec) {
        this.limit = limit; // 设置每个窗口允许的最大请求数
        this.windowSizeInMs = TimeUnit.SECONDS.toMillis(windowSizeInSec); // 将窗口大小从秒转换为毫秒
        this.count = new AtomicInteger(0); // 初始化计数器为0
        this.lastResetTime = System.currentTimeMillis(); // 设置上次重置时间 为当前时间
    }
    // 尝试获取许可
    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis(); // 获取当前时间
        // 检查是否需要重置计数器
        if (currentTime - lastResetTime > windowSizeInMs) {
            count.set(0); // 重置计数器
            lastResetTime = currentTime; // 更新上次重置时间
        }
        // 尝试增加计数并检查是否超过限制
        return count.incrementAndGet() <= limit;
    }
    // 主函数，用于测试速率限制器
    public static void main(String[] args) throws InterruptedException {
        CounterRateLimiter limiter = new CounterRateLimiter(10, 1); // 创建一个每秒限制10个请求的速率限制器
        for (int i = 0; i < 15; i++) {
            System.out.println("Request " + i + ": " + limiter.tryAcquire()); // 尝试获取许可并打印结果
            Thread.sleep(10); // 模拟请求间隔
        }
    }
}