package com.example.redpackagerain.util;

import com.example.redpackagerain.enums.ThreadPoolEnum;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolExecutor {

    //核心线程数
    public static int threadsum() {
        return Runtime.getRuntime().availableProcessors();
    }

    private static final Map<ThreadPoolEnum, org.apache.tomcat.util.threads.ThreadPoolExecutor> strategyMaps = new ConcurrentHashMap();

    public static final ExecutorService executorService = Executors.newFixedThreadPool(threadsum());

    //使用自定义线程池并行执行
    static org.apache.tomcat.util.threads.ThreadPoolExecutor executor = new org.apache.tomcat.util.threads.ThreadPoolExecutor(
            threadsum() * 2, // 核心线程数:io类型的任务通常建议设置为CPU核心数的两倍左右
            (threadsum() * 2) * 4 , // 最大线程数：io类型的任务建议设置为核心线程数的2-4倍
            60, // 空闲线程存活时间
            TimeUnit.SECONDS, // 时间单位
            new LinkedBlockingQueue<>(10000) // 队列容量
    );

    //获取自定义线程池
    public static org.apache.tomcat.util.threads.ThreadPoolExecutor getExecutor(ThreadPoolEnum threadPoolEnum){
        org.apache.tomcat.util.threads.ThreadPoolExecutor threadPoolExecutor = strategyMaps.get(threadPoolEnum);
        if(threadPoolExecutor == null) {
            strategyMaps.put(threadPoolEnum, executor);
            return strategyMaps.get(threadPoolEnum);
        }else {
            return threadPoolExecutor;
        }
    }


    //获取自定义线程池
    public static org.apache.tomcat.util.threads.ThreadPoolExecutor getThreadPoolExecutor(){
        return getExecutor(ThreadPoolEnum.THREADPOOL);
    }

}
