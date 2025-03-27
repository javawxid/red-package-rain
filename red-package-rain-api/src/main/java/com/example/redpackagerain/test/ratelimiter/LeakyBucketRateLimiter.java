package com.example.redpackagerain.test.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// 定义一个漏桶速率限制器类
public class LeakyBucketRateLimiter {
    // 漏桶的容量
    private final long capacity;
    // 每毫秒泄漏的速率
    private final long leakRatePerMs;
    // 当前漏桶中的水量
    private final AtomicLong water;
    // 用于同步的锁
    private final Lock lock;
    // 上次泄漏的时间
    private long lastLeakTime;
    // 构造函数，初始化漏桶速率限制器
    public LeakyBucketRateLimiter(long capacity, long leakRatePerSec) {
        this.capacity = capacity; // 设置漏桶的容量
        this.leakRatePerMs = leakRatePerSec / 1000; // 将每秒泄漏的速率转换为每毫秒泄漏的速率
        this.water = new AtomicLong(0); // 初始化漏桶中的水量为0
        this.lock = new ReentrantLock(); // 初始化锁
        this.lastLeakTime = System.currentTimeMillis(); // 设置上次泄漏的时间为当前时间
    }
    // 尝试获取许可的方法
    public boolean tryAcquire() {
        lock.lock(); // 获取锁
        try {
            long currentTime = System.currentTimeMillis(); // 获取当前时间
            long leaked = (currentTime - lastLeakTime) * leakRatePerMs; // 计算自上次泄漏以来泄漏的水量
            water.set(Math.max(0, water.get() - leaked)); // 更新漏桶中的水量，但不会小于0
            lastLeakTime = currentTime; // 更新上次泄漏的时间
            if (water.get() < capacity) { // 如果漏桶中的水量小于容量
                water.incrementAndGet(); // 增加水量
                return true; // 允许获取许可
            }
            return false; // 如果漏桶中的水量达到或超过容量，则不允许获取许可
        } finally {
            lock.unlock(); // 释放锁
        }
    }
    // 主函数，用于测试漏桶速率限制器
    public static void main(String[] args) throws InterruptedException {
        LeakyBucketRateLimiter limiter = new LeakyBucketRateLimiter(10, 10); // 创建一个容量为10，每秒泄漏10个单位的漏桶速率限制器
        for (int i = 0; i < 15; i++) {
            System.out.println("Request " + i + ": " + limiter.tryAcquire()); // 尝试获取许可并打印结果
            Thread.sleep(10); // 模拟请求间隔
        }
    }
}