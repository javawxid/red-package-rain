//package com.yunxi.user.dynamic;
//
//import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
//import com.yunxi.user.algorithem.RobRedPackageLogComplexDSSharding;
//import com.yunxi.user.algorithem.RobRedPackageLogComplexTableSharding;
//import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
//import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
//import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
//import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
//import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//@Configuration
//public class DataSourceConfig {
//    @Primary//将Bean设置为主要注入Bean
//    @Bean(name = "primaryDataSource")
//    @ConfigurationProperties("spring.datasource.primary")
//    public DataSource primaryDataSource() {
//        // 底层会自动拿到spring.datasource中的配置， 创建一个DruidDataSource
//        return DruidDataSourceBuilder.create().build();
///*        return DataSourceBuilder.create()
//                .url("jdbc:mysql://192.168.80.101:33061/user?characterEncoding=UTF-8&allowMultiQueries=true&serverTimezone=GMT%2B8")
//                .username("root")
//                .password("node1master1root")
//                .driverClassName("com.mysql.cj.jdbc.Driver")
//                .type(com.alibaba.druid.pool.DruidDataSource.class)
//                .build();*/
//    }
//    @Bean(name = "customShardingDataSource")
//    public DataSource customShardingDataSource() throws Exception {
//        // 配置真实数据源
//        Map<String, DataSource> dataSourceMap = new HashMap<>();
//        dataSourceMap.put("m1", createDataSource("m1"));
//        dataSourceMap.put("m2", createDataSource("m2"));
//        // 配置分片规则
//        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
//        shardingRuleConfig.getTableRuleConfigs().add(getTableRuleConfiguration());
//        shardingRuleConfig.getBindingTableGroups().add("red_package_log");
//        shardingRuleConfig.getBroadcastTables().add("tb_red_package_log");
//        // 配置属性
//        Properties properties = new Properties();
//        properties.setProperty("sql.show", "true");
//        // 创建 ShardingDataSource
//        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, properties);
//    }
//    @Bean
//    public DataSource dynamicDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource,@Qualifier("customShardingDataSource") DataSource customShardingDataSource) {
//        Map<Object, Object> targetDataSources = new HashMap<>();
//        targetDataSources.put("primaryDataSource", primaryDataSource);
//        targetDataSources.put("customShardingDataSource", customShardingDataSource);
//        DynamicDataSource dynamicDataSource = new DynamicDataSource();
//        dynamicDataSource.setTargetDataSources(targetDataSources);
//        dynamicDataSource.setDefaultTargetDataSource(primaryDataSource);
//        return dynamicDataSource;
//    }
//    private DataSource createDataSource(String dataSourceName) {
//        Integer integer = Integer.valueOf(dataSourceName.substring(1));
//        String password;
//        if (integer == 1) {
//            password = "node1master1root";
//        }else {
//            password = "node2slave1root";
//        }
//        return DataSourceBuilder.create()
//                .url("jdbc:mysql://192.168.80.10" + integer + ":3306" + integer + "/red_package?serverTimezone=GMT%2B8")
//                .username("root")
//                .password(password)
//                .driverClassName("com.mysql.cj.jdbc.Driver")
//                .type(com.alibaba.druid.pool.DruidDataSource.class)
//                .build();
//    }
//
//    @Resource
//    RobRedPackageLogComplexDSSharding robRedPackageLogComplexDSSharding;
//    @Resource
//    RobRedPackageLogComplexTableSharding robRedPackageLogComplexTableSharding;
//
//    private TableRuleConfiguration getTableRuleConfiguration() {
////        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration("red_package_log", "m$->{1..2}.red_package_log_$->{2020..2024}_$->{1..12}_$->{1..2}");
//        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration("red_package_log", "m$->{1..2}.red_package_log_$->{1..2}");
//        tableRuleConfig.setDatabaseShardingStrategyConfig(new ComplexShardingStrategyConfiguration("user_id", robRedPackageLogComplexDSSharding));
////        tableRuleConfig.setTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration("year, month, userId", robRedPackageLogComplexTableSharding));
//        tableRuleConfig.setTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration("userId", robRedPackageLogComplexTableSharding));
//        tableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("LOGKEY", "userId"));
//        return tableRuleConfig;
//    }
//
//}