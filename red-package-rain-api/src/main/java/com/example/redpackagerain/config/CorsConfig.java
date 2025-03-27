package com.example.redpackagerain.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * 跨域配置类
 */
@Component
public class CorsConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")      // 添加路径规则
                .allowCredentials(true)               // 是否允许在跨域的情况下传递Cookie
                .allowedOrigins("*")           // 允许请求来源的域规则
                .allowedMethods("*")
                .allowedHeaders("*");                  // 允许所有的请求头
    }
}