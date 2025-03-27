//package com.yunxi.user.dynamic;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//import javax.sql.DataSource;
//
///**
// * DataSourceConfig.java一起配置
// */
//@Configuration
//public class JdbcTemplateConfig {
//    @Bean
//    public JdbcTemplate jdbcTemplate(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) {
//        return new JdbcTemplate(dynamicDataSource);
//    }
//}