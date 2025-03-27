//package com.yunxi.user.dynamic;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//public class DynamicDataSource extends AbstractRoutingDataSource {
//    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);
//    @Override
//    protected Object determineCurrentLookupKey() {
//        String dataSourceType = DynamicDataSourceContextHolder.getDataSourceType();
//        logger.info("Current DataSource Type: {}", dataSourceType);
//        return dataSourceType;
//    }
//}