package com.example.redpackagerain.test.ratelimiter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
// 定义一个滑动日志速率限制器类
public class SlidingLogRateLimiter {
    // 每个窗口允许的最大请求数
    private final int limit;
    // 窗口大小（毫秒）
    private final long windowSizeInMs;
    // 用于存储请求时间的日志队列
    private final Queue<Long> log;
    // 构造函数，初始化速率限制器
    public SlidingLogRateLimiter(int limit, long windowSizeInSec) {
        this.limit = limit; // 设置每窗口允许的最大请求数
        this.windowSizeInMs = TimeUnit.SECONDS.toMillis(windowSizeInSec); // 将窗口大小从秒转换为毫秒
        this.log = new LinkedList<>(); // 初始化日志队列
    }
    // 尝试获取许可，使用synchronized保证线程安全
    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis(); // 获取当前时间
        // 移除窗口外的请求记录
        while (!log.isEmpty() && currentTime - log.peek() > windowSizeInMs) {
            log.poll(); // 移除队列头部的过期记录
        }
        // 检查当前窗口内的请求数是否小于限制
        if (log.size() < limit) {
            log.offer(currentTime); // 将当前请求时间添加到队列尾部
            return true; // 获取许可成功
        }
        return false; // 获取许可失败
    }
    // 主函数，用于测试速率限制器
    public static void main(String[] args) throws InterruptedException {
        SlidingLogRateLimiter limiter = new SlidingLogRateLimiter(10, 1); // 创建一个每秒限制10个请求的速率限制器
        for (int i = 0; i < 15; i++) {
            System.out.println("Request " + i + ": " + limiter.tryAcquire()); // 尝试获取许可并打印结果
            Thread.sleep(10); // 模拟请求间隔
        }
    }
}