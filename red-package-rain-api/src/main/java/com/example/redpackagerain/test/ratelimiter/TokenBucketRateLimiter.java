package com.example.redpackagerain.test.ratelimiter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// 定义一个令牌桶速率限制器类
public class TokenBucketRateLimiter {
    // 令牌桶的容量，即桶中最多可以存储的令牌数量
    private final long capacity;
    // 每毫秒新增的令牌数量
    private final long tokensPerMs;
    // 当前令牌桶中剩余的令牌数量
    private final AtomicLong tokens;
    // 用于同步的锁，确保对令牌桶操作的原子性和可见性
    private final Lock lock;
    // 上次补充令牌的时间
    private long lastRefillTime;
    // 构造函数，初始化令牌桶速率限制器
    public TokenBucketRateLimiter(long capacity, long tokensPerSec) {
        this.capacity = capacity; // 设置令牌桶的容量
        this.tokensPerMs = tokensPerSec / 1000; // 将每秒新增的令牌数量转换为每毫秒新增的令牌数量
        this.tokens = new AtomicLong(capacity); // 初始化令牌桶中的令牌数量为容量值
        this.lock = new ReentrantLock(); // 创建一个可重入锁
        this.lastRefillTime = System.currentTimeMillis(); // 设置上次补充令牌的时间为当前时间
    }
    // 尝试获取令牌的方法
    public boolean tryAcquire() {
        lock.lock(); // 获取锁，保证以下操作的原子性
        try {
            long currentTime = System.currentTimeMillis(); // 获取当前时间
            long newTokens = (currentTime - lastRefillTime) * tokensPerMs; // 计算自上次补充令牌以来新增的令牌数量
            tokens.set(Math.min(capacity, tokens.get() + newTokens)); // 更新令牌桶中的令牌数量，但不会超过容量
            lastRefillTime = currentTime; // 更新上次补充令牌的时间
            if (tokens.get() > 0) { // 如果令牌桶中还有令牌
                tokens.decrementAndGet(); // 减少一个令牌
                return true; // 返回true，表示成功获取到令牌
            }
            return false; // 如果令牌桶中没有令牌，返回false，表示获取令牌失败
        } finally {
            lock.unlock(); // 释放锁，无论操作成功还是失败
        }
    }
    // 主函数，用于测试令牌桶速率限制器
    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10, 10); // 创建一个容量为10，每秒补充10个令牌的令牌桶速率限制器
        for (int i = 0; i < 15; i++) {
            System.out.println("Request " + i + ": " + limiter.tryAcquire()); // 尝试获取令牌并打印结果
            Thread.sleep(10); // 模拟请求间隔
        }
    }
}