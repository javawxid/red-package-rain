package com.example.springcloudsecurityoauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @Author: liaozhiwei
 * @Description: 第三种方式：redis存储
 * @Date: Created in 19:09 2022/8/23
 */
@Configuration
public class RedisStoreConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

     //第三种：使用密码模式需要配置（使用redis的方式）
//    @Bean
//    public TokenStore redisTokenStore(){
//        // access_token
//        return new RedisTokenStore(redisConnectionFactory);
//    }
}
