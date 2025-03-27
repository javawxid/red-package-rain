package com.yunxi.user.property;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhiweiLiao
 * @Description
 * @Date create in 2022/10/17 0017 17:31
 */
@Data
@Configuration
public class Property {

    @Value("${baidu.clientId}")
    String clientId;

    @Value("${baidu.clientSecret}")
    String clientSecret;

    @Value("${appid}")
    String appidValue;

    @Value("${secret}")
    String secretValue;

    @Value("${security.environment}")
    String securityEnvironment;
}
