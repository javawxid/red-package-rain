package com.example.springcloudsecurityoauth2.config;

import com.example.springcloudsecurityoauth2.properties.JwtCAProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * @Author: liaozhiwei
 * @Description: jwt.jks文件形式存储
 * @Date: Created in 19:09 2022/8/23
 */

@Configuration
@EnableConfigurationProperties(value = JwtCAProperties.class)
public class JwtTokenStoreConfig {

      //第二种方式：使用jwt文件的方式
    @Bean
    public TokenStore jwtTokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public AuthTokenEnhancer authTokenEnhancer() {
        return new AuthTokenEnhancer();
    }

    @Autowired
    private JwtCAProperties jwtCAProperties;

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource(jwtCAProperties.getKeyPairName()),
                jwtCAProperties.getKeyPairSecret().toCharArray());
        return keyStoreKeyFactory.getKeyPair(jwtCAProperties.getKeyPairAlias(),
                jwtCAProperties.getKeyPairStoreSecret().toCharArray());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        //配置JWT使用的秘钥 非对称加密
        accessTokenConverter.setKeyPair(keyPair());
        return accessTokenConverter;
    }


}
