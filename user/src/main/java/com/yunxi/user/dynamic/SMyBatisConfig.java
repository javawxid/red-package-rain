package com.yunxi.user.dynamic;

import com.yunxi.user.algorithem.RobRedPackageLogComplexDSSharding;
import com.yunxi.user.algorithem.RobRedPackageLogComplexTableSharding;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = "com.yunxi.user.mapper.s",sqlSessionFactoryRef="sSqlSessionFactory")
public class SMyBatisConfig {
    @ConfigurationProperties("spring.shardingsphere.datasource")
    @Bean(name = "customShardingDataSource")
    public DataSource customShardingDataSource() throws Exception {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("m1", createDataSource("m1"));
        dataSourceMap.put("m2", createDataSource("m2"));
        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("tb_red_package_log");
//        shardingRuleConfig.getBroadcastTables().add("tb_red_package_log");
        // 配置属性
        Properties properties = new Properties();
        properties.setProperty("sql.show", "true");
        // 创建 ShardingDataSource
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, properties);
    }
    private DataSource createDataSource(String dataSourceName) {
        Integer integer = Integer.valueOf(dataSourceName.substring(1));
        String password;
        if (integer == 1) {
            password = "node1master1root";
        }else {
            password = "node2slave1root";
        }
        return DataSourceBuilder.create()
                .url("jdbc:mysql://192.168.80.10" + integer + ":3306" + integer + "/red_package?serverTimezone=GMT%2B8")
                .username("root")
                .password(password)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .type(com.alibaba.druid.pool.DruidDataSource.class)
                .build();
    }

    private TableRuleConfiguration getTableRuleConfiguration() {
        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration("tb_red_package_log", "m$->{1..2}.tb_red_package_log_$->{2023..2027}_$->{1..12}_$->{1..2}");
//        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration("tb_red_package_log", "m$->{1..2}.tb_red_package_log_$->{1..2}");
//        tableRuleConfig.setTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration("userId", new RobRedPackageLogComplexTableSharding()));
        tableRuleConfig.setDatabaseShardingStrategyConfig(new ComplexShardingStrategyConfiguration("user_id", new RobRedPackageLogComplexDSSharding()));
        tableRuleConfig.setTableShardingStrategyConfig(new ComplexShardingStrategyConfiguration("red_package_id", new RobRedPackageLogComplexTableSharding()));
        tableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("LOGKEY", "red_package_id"));
        return tableRuleConfig;
    }
    @Bean
    public SqlSessionFactory sSqlSessionFactory() throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(customShardingDataSource());
        // 指定主库对应的mapper.xml文件
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/s/*.xml"));
        return sessionFactory.getObject();
    }
    @Bean
    public DataSourceTransactionManager stransactionManager() throws Exception {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(customShardingDataSource());
        return dataSourceTransactionManager;
    }
    @Bean
    public TransactionTemplate stransactionTemplate() throws Exception {
        return new TransactionTemplate(stransactionManager());
    }
}
