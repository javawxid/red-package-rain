package com.example.redpackagerain.controller;

import com.example.redpackagerain.constant.RedPackageRainConstant;
import com.example.redpackagerain.model.RedPackageVo;
import com.example.redpackagerain.util.WebSocketRemoteContainerUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scripting.support.StaticScriptSource;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 前端轮询：这是一种最基础的方式，浏览器定期向服务器发送请求以获取最新数据。这种方式简单但效率不高，因为每次请求都需要建立连接和断开连接，而且服务器可能需要在没有新数据的情况下处理大量的空请求。
 单向SSE：SSE是一种允许服务器向客户端推送数据的技术，但与WebSocket不同，它只支持单向通信（从服务器到客户端）。SSE的优点在于它是一种轻量级协议，相对简单，并且服务器可以在数据更新时主动将新数据推送给客户端。但是，由于它是单向的，因此客户端不能主动向服务器发送数据。
 WebSocket：WebSocket是一种全双工通信协议，它允许服务器和客户端在连接打开的情况下实时通信。这意味着客户端和服务器都可以在任何时候发送或接收数据，无需轮询。WebSocket的优点在于它提供了双向通信的能力，使得实时应用（如聊天应用、多人协同编辑等）的实现更加容易。
 要保证WebSocket长连接的可靠性，可以采取以下措施：
 心跳检测：客户端和服务器可以定期发送心跳消息来检测连接是否仍然活跃。如果连接断开，客户端可以重新建立连接。
 断线重连：当连接断开时，客户端可以尝试重新建立连接。这可以通过在WebSocket连接中添加重连逻辑来实现。
 错误处理：在WebSocket连接中，可以添加错误处理逻辑来处理连接错误、消息错误等。
 假设每个WebSocket消息平均大小为1KB（这可能会根据实际使用情况进行调整），并且每个连接每秒发送一个消息，那么理论上可以支持的上限是100,000,000 / 1024 = 97,656个连接。然而，这只是一个非常粗略的估计，实际带宽的使用会受到许多因素的影响，包括消息频率、消息大小、连接数量等。
 * WebSocket接口测试工具：http://www.jsons.cn/websocket/
 * 接口地址：ws://localhost:8057/api/websocket/{activityId}/{userId}
 */
@Slf4j
@ServerEndpoint(value = "/api/websocket/{activityId}/{userId}")
@Component
public class WebSocketApiController {

    private static RedisTemplate redisTemplate;


    // 心跳检测间隔（毫秒）
    private static final long HEARTBEAT_INTERVAL = 5000;

    // 客户端会话超时时间（毫秒），如果在这段时间内没有收到心跳消息，则认为客户端断开
    private static final long CLIENT_SESSION_TIMEOUT = HEARTBEAT_INTERVAL * 3;

    // 用于存储客户端会话和最后心跳时间的映射
    private Map<String, Long> sessionHeartbeatMap = new ConcurrentHashMap<>();

    /**
     * @Scheduled 注解来定期执行这个方法。
     * 这个方法会检查sessionHeartbeatMap中的每个会话，如果某个会话的最后心跳时间距离当前时间超过了CLIENT_SESSION_TIMEOUT，则认为该会话已超时，并从映射中移除它。
     * 客户端需要实现心跳消息的发送。这通常通过在客户端设置一个定时器来完成，定期向服务器发送一个特殊的心跳消息。
     */
    // 用于检查并处理超时会话的定时任务
    @Scheduled(fixedRate = CLIENT_SESSION_TIMEOUT)
    public void checkHeartbeat() {
        long currentTime = System.currentTimeMillis();
        sessionHeartbeatMap.entrySet().removeIf(entry -> {
            Long lastHeartbeatTime = entry.getValue();
            return currentTime - lastHeartbeatTime > CLIENT_SESSION_TIMEOUT;
        });
        //TODO 对于移除的会话，可以触发重连逻辑或进行其他处理
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        WebSocketApiController.redisTemplate = redisTemplate;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("activityId") String activityId, @PathParam("userId") String userId) {
        log.info("连接成功，活动：{}，用户：{}", activityId, userId);
        //添加userId与session关联
        WebSocketRemoteContainerUtil.addSession(userId, session);
        //添加userId与活动关联
        WebSocketRemoteContainerUtil.addUserIdToActivity(activityId, userId, redisTemplate);
        //活动开始后，新进入的用户，直接通知活动开始（活动开始后redPackgeVo对象才会生成）
        RedPackageVo redPackgeVo = getRedPackageVoFromRedis(activityId,redisTemplate);
        if (null != redPackgeVo) {
            //活动开始后，同一个用户不能重复开启红包雨
            Object object = redisTemplate.opsForHash().get(RedPackageRainConstant.RED_PACKAGE_USER_KEY + activityId, userId);
            //没有参与过活动
            if (null == object) {
                //通知当前用户开启红包雨活动
                log.info("单发消息 活动开始中，用户中途进来。userId:{},redPackgeVo:{}",userId,redPackgeVo);
                WebSocketRemoteContainerUtil.sendMsg(session, userId, redPackgeVo, redisTemplate);
            }
        }
        // 添加会话和最后心跳时间的映射
        sessionHeartbeatMap.put(session.getId(), System.currentTimeMillis());
    }

    public static RedPackageVo getRedPackageVoFromRedis(String activityId, RedisTemplate<String, String> redisTemplate) {
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置可见性，以便序列化私有字段
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        String luaScript = "return redis.call('GET', KEYS[1])";
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new StaticScriptSource(luaScript));
        redisScript.setResultType(String.class); // 设定返回类型为String
        // 执行Lua脚本
        String jsonString = redisTemplate.execute(redisScript, Collections.singletonList(RedPackageRainConstant.RED_PACKAGE_INFO_KEY + activityId));
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        // 反序列化JSON字符串为RedPackageVo对象
        try {
            RedPackageVo redPackageVo = objectMapper.readValue(jsonString, RedPackageVo.class);
            return redPackageVo;
        } catch (IOException e) {
            // 处理反序列化错误
            throw new RuntimeException("Error deserializing RedPackageVo from Redis", e);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("activityId") String activityId, @PathParam("userId") String userId) {
        log.info("退出连接，活动：{}，用户：{}", activityId, userId);
        WebSocketRemoteContainerUtil.removeSession(userId);
        WebSocketRemoteContainerUtil.removeUserIdToActivity(activityId, userId, redisTemplate);
        // 移除会话和最后心跳时间的映射
        sessionHeartbeatMap.remove(session.getId());
    }

    /**
     * 发生错误时调用+
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 处理客户端发送的心跳消息
     */
    @OnMessage
    public void onHeartbeat(String message, Session session) {
        // 更新会话的最后心跳时间
        sessionHeartbeatMap.put(session.getId(), System.currentTimeMillis());
        log.debug("接收到心跳消息: {} 来自会话: {}", message, session.getId());
    }

}
