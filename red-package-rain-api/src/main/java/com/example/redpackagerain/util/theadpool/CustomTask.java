package com.example.redpackagerain.util.theadpool;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
public class CustomTask implements Runnable{

    Object service;
    Method method;
    Object[] param;

    //重试纪元 创建一个原子整型变量并初始化为0
    private AtomicInteger retryEpoch = new AtomicInteger(0);

    /**
     * @param service        要调用的service
     * @param method         被调用的方法
     * @param param          方法参数
     */
    public CustomTask(Object service, Method method, Object... param) {
        this.service = service;
        this.method = method;
        this.param = param;
    }

    @Override
    public void run() {
        try {
            Long start = System.currentTimeMillis();
            method.invoke(service, param);
            Long end = System.currentTimeMillis();
            log.info(String.format("%s *** 执行 ((( %s ))) 方法,耗时 <<< %s 豪秒 >>> 参数",service.getClass(),method.getName(),(end - start), JSONObject.toJSONString(param)));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
