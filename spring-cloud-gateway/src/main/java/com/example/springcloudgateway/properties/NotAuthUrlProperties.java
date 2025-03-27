package com.example.springcloudgateway.properties;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.LinkedHashSet;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 11:07 2022/8/25
 */

@Data
@ConfigurationProperties("auth.gateway")
public class NotAuthUrlProperties {

    private LinkedHashSet<String> shouldSkipUrls;
}
