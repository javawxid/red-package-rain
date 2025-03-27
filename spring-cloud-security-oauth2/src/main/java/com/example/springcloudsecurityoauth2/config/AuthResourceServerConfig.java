package com.example.springcloudsecurityoauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @Author: liaozhiwei
 * @Description: 资源服务配置
 * @Date: Created in 18:19 2022/8/23
 */
@Configuration
@EnableResourceServer
public class AuthResourceServerConfig  extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
      //利用RequestMatcher对象来进行路径匹配了，调用了antMatchers方法来定义什么样的请求可以放过，什么样的请求需要验证
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and().requestMatchers().antMatchers("/user/**");
    }
}
