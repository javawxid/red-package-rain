package com.example.redpackagerain.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.redpackagerain.constant.RedPackageRainConstant;
import com.example.redpackagerain.model.RedPackageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.util.CollectionUtils;

import javax.websocket.Session;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket工具类
 */
@Slf4j
public class WebSocketRemoteContainerUtil {

    //保存用户与Session关系
    // 注意：websocket的Session不能序列化到redis，只能在内存中保存，集群环境配合redis广播，通知所有用户
    private static Map<String, Session> userIdToSessionMap = new ConcurrentHashMap<>();

    public static void addSession(String userId, Session session) {
        userIdToSessionMap.put(userId, session);
    }

    public static void removeSession(String userId) {
        userIdToSessionMap.remove(userId);
    }

    public static Session getSession(String userId) {
        return userIdToSessionMap.get(userId);
    }



    public static void removeUserIdToActivity(String activityId, String userId, RedisTemplate redisTemplate) {
        redisTemplate.boundSetOps(RedPackageRainConstant.RED_PACKAGE_ACTIVITY_KEY + activityId).remove(userId);
    }

    public static void addUserIdToActivity(String activityId, String userId, RedisTemplate redisTemplate) {
        log.info("----------添加用户:{}，活动:{}",userId,activityId);
        redisTemplate.boundSetOps(RedPackageRainConstant.RED_PACKAGE_ACTIVITY_KEY + activityId).add(userId);
    }

    public static Set getActivityUser(String activityId, RedisTemplate redisTemplate) {
        log.info("----------活动Id:{}",activityId);
        String key = RedPackageRainConstant.RED_PACKAGE_ACTIVITY_KEY + activityId;
        // 获取 Redis 的 Set 操作绑定
        return redisTemplate.boundSetOps(key).members();
    }

    /**
     * 群发消息 活动开始前，有人进入等待厅准备抢红包
     * @param redPackgeVo
     * @param redisTemplate
     */
    public static void sendMsg(RedPackageVo redPackgeVo, RedisTemplate redisTemplate) {
        Set<String> userSet = getActivityUser(redPackgeVo.getActivityId(), redisTemplate);
        log.info("接收人：{}", JSON.toJSONString(userSet));
        if (!CollectionUtils.isEmpty(userSet)) {
            for (String userId : userSet) {
                Session session = WebSocketRemoteContainerUtil.getSession(userId);
                if (null != session) {
                    session.getAsyncRemote().sendText(JSON.toJSONString(redPackgeVo, SerializerFeature.DisableCircularReferenceDetect));//异步发送消息.
                    //记录用户已开启
                    redisTemplate.opsForHash().put(RedPackageRainConstant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityId(), userId, 1);
                    //设置过期时间
                    redisTemplate.expire(RedPackageRainConstant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityId(), redPackgeVo.getDuration() + 10000, TimeUnit.MILLISECONDS);
                    //将上述二行修改成lua脚本执行，保证原子性
//                    recordUserStartedAndExpire(redPackgeVo,userId,redisTemplate);
                }
            }
        }
    }

    public static void recordUserStartedAndExpire(RedPackageVo redPackgeVo, String userId, RedisTemplate<String, Object> redisTemplate) {
        String luaScript = "redis.call('HSET', KEYS[1], ARGV[1], ARGV[2]);" +
                "redis.call('EXPIRE', KEYS[1], ARGV[3]);" +
                "return { redis.call('HGET', KEYS[1], ARGV[1]), redis.call('TTL', KEYS[1]) };";
        DefaultRedisScript<Object[]> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new StaticScriptSource(luaScript));
        // 执行Lua脚本
        redisTemplate.execute(redisScript,
                Collections.singletonList(RedPackageRainConstant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityId()),
                userId, 1, redPackgeVo.getDuration() + 10000 // 传递userId, 值1, 和过期时间作为ARGV参数
        );
    }


//    public static void sendMsg(Session session, String token, RedPackageVo redPackgeVo, RedisTemplate redisTemplate) {
//        session.getAsyncRemote().sendText(JSON.toJSONString(redPackgeVo, SerializerFeature.DisableCircularReferenceDetect));//异步发送消息.
//        //记录用户已开启
//        redisTemplate.opsForHash().put(RedPackageRainConstant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityId(), token, 1);
//        //设置过期时间
//        redisTemplate.expire(RedPackageRainConstant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityId(), redPackgeVo.getDuration()+10000, TimeUnit.MILLISECONDS);
//    }
    /**
     * 单发消息 活动开始中，用户中途进来
     * @param session
     * @param redPackgeVo
     * @param redisTemplate
     */
    public static void sendMsg(Session session, String token, RedPackageVo redPackgeVo, RedisTemplate<String, Object> redisTemplate) {
        // 异步发送消息，这不在Lua脚本中处理
        session.getAsyncRemote().sendText(JSON.toJSONString(redPackgeVo, SerializerFeature.DisableCircularReferenceDetect));
        // 定义Lua脚本
        String luaScript = "-- Lua脚本，用于记录用户已开启红包活动并设置过期时间  \n" +
                "-- KEYS[1] 是用户活动ID对应的Redis hash key  \n" +
                "-- ARGV[1] 是用户的token  \n" +
                "-- ARGV[2] 是用户活动的持续时间（毫秒）的字符串表示  \n" +
                "-- ARGV[3] 是序列化后的RedPackageVo对象，用于异步发送消息（尽管发送消息的操作不在Redis中执行，但你可以在Java代码中执行它）  \n" +
                "  \n" +
                "-- 记录用户已开启红包活动  \n" +
                "redis.call('HSET', KEYS[1], ARGV[1], 1)  \n" +
                "  \n" +
                "-- 检查ARGV[2]是否为nil，并转换为数字  \n" +
                "local durationStr = ARGV[2]  \n" +
                "if durationStr then  \n" +
                "    local duration = tonumber(durationStr)  \n" +
                "    if duration then  \n" +
                "        -- 设置过期时间，注意：持续时间需要加上额外的10000毫秒  \n" +
                "        local expirationTime = math.floor((duration + 10000) / 1000) -- EXPIRE 需要秒作为参数，所以我们将毫秒转换为秒  \n" +
                "        redis.call('EXPIRE', KEYS[1], expirationTime)  \n" +
                "    else  \n" +
                "        -- 如果duration不是数字，你可以选择记录错误、抛出异常或执行其他逻辑  \n" +
                "        -- 这里我们简单地返回nil，但实际情况中你可能需要更复杂的错误处理  \n" +
                "        return nil  \n" +
                "    end  \n" +
                "else  \n" +
                "    -- 如果durationStr是nil，同样地，你可以选择记录错误、抛出异常或执行其他逻辑  \n" +
                "    -- 这里我们简单地返回nil  \n" +
                "    return nil  \n" +
                "end  \n" +
                "  \n" +
                "-- 此脚本不返回任何有用值给调用者，但执行了必要的Redis命令  \n" +
                "return nil";
        // 创建DefaultRedisScript实例
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new StaticScriptSource(luaScript));
        redisScript.setResultType(Long.class); // 因为脚本不返回任何值，这里可以设置为任意类型或null
        // 执行Lua脚本
        redisTemplate.execute(
                redisScript,
                Collections.singletonList(RedPackageRainConstant.RED_PACKAGE_USER_KEY + redPackgeVo.getActivityId()),
                token, // ARGV[1]
                String.valueOf(redPackgeVo.getDuration()) // ARGV[2]
        );
    }
}
