package com.example.springcloudgateway.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 11:15 2022/8/25
 */

@Component
public  class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    public static RedisTemplate redis;

    @PostConstruct
    public void getRedisTemplate(){
        redis = this.redisTemplate;
    }

    /**
     * 加锁 lockKey（锁的键），value（锁的值），和expireTime（锁的过期时间，以秒为单位）
     * @param lockKey
     * @param value
     * @param expireTime  默认是秒
     * @return
     */
    private static final String GET_LOCK_SCRIPT =
            "if redis.call('set', KEYS[1], ARGV[1], 'NX', 'EX', ARGV[2]) then " +
                    "  return 1 " +
                    "else " +
                    "  return 0 " +
                    "end";

    public static boolean getLock(String lockKey, String requestId, long expireTime) {
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(GET_LOCK_SCRIPT, Long.class);
            Object result = RedisUtil.redis.execute(redisScript,Collections.singletonList(lockKey), requestId, expireTime); // 直接传递整数expireTime
            // 检查返回值是否为1，如果是，则表示成功获取锁
            // 检查Object是否是Number类型的实例
            if (result != null && result instanceof Long) {
                Long value = (Long) result;
                return value == 1L;
            }
        } catch (Exception e) {
            // 异常处理，可能需要记录日志或执行其他恢复操作
            e.printStackTrace();
            // 在出现异常的情况下，可以尝试释放锁（尽管可能并没有成功获取锁）
            // 释放锁的代码可以在这里调用，但需要谨慎处理以避免误解锁
            return false;
        } finally {
            // 确保连接被正确释放
            RedisConnectionUtils.unbindConnection(RedisUtil.redis.getConnectionFactory());
        }
        return false;
    }

    /**
     * 释放锁
     * @param lockKey
     * @param value
     * @return
     */
    private static final String RELEASE_LOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "  return redis.call('del', KEYS[1]) " +
                    "else " +
                    "  return 0 " +
                    "end";

    public static boolean releaseLock(String lockKey, String requestId) {
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_SCRIPT, Long.class);
            Object result = RedisUtil.redis.execute(redisScript, Collections.singletonList(lockKey), requestId);
            // 检查返回值是否为1，如果是，则表示成功释放锁
            if (result != null && result instanceof Long) {
                Long value = (Long) result;
                return value == 1L;
            }
        } catch (Exception e) {
            // 异常处理，可能需要记录日志或执行其他恢复操作
            e.printStackTrace();
            return false;
        } finally {
            // 确保连接被正确释放
            RedisConnectionUtils.unbindConnection(RedisUtil.redis.getConnectionFactory());
        }
        return false;
    }
}
