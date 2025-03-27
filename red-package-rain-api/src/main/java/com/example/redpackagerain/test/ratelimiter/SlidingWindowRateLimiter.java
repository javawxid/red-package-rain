package com.example.redpackagerain.test.ratelimiter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
// 定义一个滑动窗口速率限制器类
public class SlidingWindowRateLimiter {
    // 每个窗口允许的最大请求数
    private final int limit;
    // 窗口大小（毫秒）
    private final long windowSizeInMs;
    // 请求时间的队列
    private final Queue<Long> requests;
    // 构造函数，初始化速率限制器
    public SlidingWindowRateLimiter(int limit, long windowSizeInSec) {
        this.limit = limit; // 设置每个窗口允许的最大请求数
        this.windowSizeInMs = TimeUnit.SECONDS.toMillis(windowSizeInSec); // 将窗口大小从秒转换为毫秒
        this.requests = new LinkedList<>(); // 创建一个请求时间的队列
    }
    // 尝试获取许可的方法
    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis(); // 获取当前时间
        // 移除窗口外的请求记录
        while (!requests.isEmpty() && currentTime - requests.peek() > windowSizeInMs) {
            requests.poll(); // 从队列头部移除超时的请求记录
        }
        // 如果当前窗口内的请求数小于限制，则添加当前请求时间到队列并返回true
        if (requests.size() < limit) {
            requests.offer(currentTime); // 将当前请求时间添加到队列尾部
            return true;
        }
        // 如果当前窗口内的请求数达到限制，则返回false
        return false;
    }
    // 主函数，用于测试滑动窗口速率限制器
    public static void main(String[] args) throws InterruptedException {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(10, 1); // 创建一个每秒限制10个请求的速率限制器
        for (int i = 0; i < 15; i++) {
            System.out.println("Request " + i + ": " + limiter.tryAcquire()); // 尝试获取许可并打印结果
            Thread.sleep(10); // 模拟请求间隔
        }
    }
}