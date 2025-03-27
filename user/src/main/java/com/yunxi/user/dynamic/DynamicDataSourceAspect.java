//package com.yunxi.user.dynamic;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//public class DynamicDataSourceAspect implements Ordered {
//    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);
//    // 在每个访问数据库的方法执行前执行。
//    @Before("within(com.yunxi.user.service.*) && @annotation(wr)")
//    public void before(JoinPoint point, WR wr){
//        String name = wr.value();
//        DynamicDataSourceContextHolder.setDataSourceType(name);
//        logger.info("====> DynamicDataSourceContextHolder.getDataSourceType:" + DynamicDataSourceContextHolder.getDataSourceType());
//    }
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
