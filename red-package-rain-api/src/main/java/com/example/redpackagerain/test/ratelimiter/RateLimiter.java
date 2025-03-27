package com.example.redpackagerain.test.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// 定义一个速率限制器类
public class RateLimiter {
    // 令牌桶的容量
    private final long capacity;
    // 每毫秒新增的令牌数
    private final long tokensPerMs;
    // 当前令牌数
    private final AtomicLong tokens;
    // 用于同步的锁
    private final Lock lock;
    // 上次补充令牌的时间
    private long lastRefillTime;
    // 构造函数，初始化速率限制器
    public RateLimiter(long capacity, long tokensPerSec) {
        this.capacity = capacity; // 设置令牌桶的容量
        this.tokensPerMs = tokensPerSec / 1000; // 将每秒新增的令牌数转换为每毫秒新增的令牌数
        this.tokens = new AtomicLong(capacity); // 初始化令牌数为容量值
        this.lock = new ReentrantLock(); // 初始化锁
        this.lastRefillTime = System.currentTimeMillis(); // 设置上次补充令牌的时间为当前时间
    }
    // 尝试获取令牌
    public boolean tryAcquire() {
        lock.lock(); // 获取锁
        try {
            long currentTime = System.currentTimeMillis(); // 获取当前时间
            long newTokens = (currentTime - lastRefillTime) * tokensPerMs; // 计算自上次补充以来新增的令牌数
            tokens.set(Math.min(capacity, tokens.get() + newTokens)); // 更新令牌数，但不超过容量
            lastRefillTime = currentTime; // 更新上次补充令牌的时间
            if (tokens.get() > 0) { // 如果当前令牌数大于0
                tokens.decrementAndGet(); // 减少一个令牌
                return true; // 获取令牌成功
            }
            return false; // 获取令牌失败
        } finally {
            lock.unlock(); // 释放锁
        }
    }
    // 主函数，用于测试速率限制器
    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = new RateLimiter(10, 10); // 创建一个容量为10，每秒补充10个令牌的速率限制器
        for (int i = 0; i < 15; i++) {
            System.out.println("Request " + i + ": " + limiter.tryAcquire()); // 尝试获取令牌并打印结果
            Thread.sleep(10); // 模拟请求间隔
        }
    }
}