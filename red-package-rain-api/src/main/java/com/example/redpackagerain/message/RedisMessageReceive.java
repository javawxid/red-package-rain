package com.example.redpackagerain.message;

import com.alibaba.fastjson.JSON;
import com.example.redpackagerain.model.RedPackageVo;
import com.example.redpackagerain.util.WebSocketRemoteContainerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis广播消息处理类
 */
@Slf4j
@Component
public class RedisMessageReceive {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 接收redis广播消息的方法
     */
    public void receiveMessage(String message) {
        log.info("----------收到消息了message：" + message);
        //序列化消息
        Object msg = redisTemplate.getValueSerializer().deserialize(message.getBytes());
        if (null != msg) {
            RedPackageVo redPackgeVo = JSON.parseObject(msg.toString(), RedPackageVo.class);
            //WebSocket发送消息
            log.info("群发消息：{}",redPackgeVo.toString());
            WebSocketRemoteContainerUtil.sendMsg(redPackgeVo, redisTemplate);
        }
    }

}
