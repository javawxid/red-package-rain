package com.yunxi.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 访问地址：http://localhost:8098/user/swagger-ui.html
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket createRestApi(){
        Contact contact = new Contact("java_wxid","","java_wxid@aliyun.com");

        ApiInfo apiInfo=new ApiInfoBuilder()
                .title("swagger接口")
                .description("用户相关接口描述")
                .version("1.0.0")
                .contact(contact)
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yunxi.user.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
