package com.example.redpackagerain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.stereotype.Indexed;

@Indexed
@EnableDiscoveryClient
@SpringBootApplication
public class RedPackageRainApiApplication {
    public static void main(String[] args) {
        // 启动Spring Boot应用程序
        SpringApplication.run(RedPackageRainApiApplication.class, args);
//        ApplicationContext context = SpringApplication.run(RedPackageRainApiApplication.class, args);
//        String[] beanNames = context.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//            System.out.println(beanName);
//        }
    }


}
