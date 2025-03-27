package com.example.springcloudsecurityoauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @Description: 读取配置文件中的属性配置
 */
//实时刷新注解
@RefreshScope
@Data
@ConfigurationProperties(prefix = "authjwt")
public class JwtCAProperties {

    /**
     * 证书名称
     */
    private String keyPairName;


    /**
     * 证书别名
     */
    private String keyPairAlias;

    /**
     * 证书私钥
     */
    private String keyPairSecret;

    /**
     * 证书存储密钥
     */
    private String keyPairStoreSecret;

}

