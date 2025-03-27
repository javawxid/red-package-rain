//package com.yunxi.user.dynamic;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//public class DynamicDataSourceContextHolder {
//    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
//    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);
//    public static void setDataSourceType(String dataSourceType) {
//        logger.info("Setting DataSource Type: {}", dataSourceType);
//        contextHolder.set(dataSourceType);
//    }
//    public static String getDataSourceType() {
//        return contextHolder.get();
//    }
//    public static void clearDataSourceType() {
//        logger.info("Clearing DataSource Type");
//        contextHolder.remove();
//    }
//}