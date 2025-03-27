package com.example.redpackagerain.test.ratelimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
@Component
public class TokenBucket {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_KEY_PREFIX = "token_bucket_";
    /**
     * 尝试消费指定数量的令牌
     * @param key 令牌桶的唯一标识
     * @param tokens 需要消费的令牌数量
     * @return 如果消费成功返回true，否则返回false
     */
    public boolean tryConsume(String key, int tokens) {
        String bucketKey = TOKEN_KEY_PREFIX + key;
        String currentTokens = redisTemplate.opsForValue().get(bucketKey);
        if (currentTokens == null) {
            // 初始化令牌桶，设置初始令牌数量为100，并设置过期时间为1小时
            redisTemplate.opsForValue().set(bucketKey, "100", 1, TimeUnit.HOURS);
            return true;
        }
        int currentTokenCount = Integer.parseInt(currentTokens);
        if (currentTokenCount >= tokens) {
            // 消费令牌
            redisTemplate.opsForValue().decrement(bucketKey, tokens);
            return true;
        } else {
            return false;
        }
    }
}