package com.example.redpackagerain.service;

import com.alibaba.fastjson.JSONObject;
import com.example.redpackagerain.producer.MessageProducer;
import com.example.redpackagerain.util.theadpool.CustomTask;
import com.example.redpackagerain.util.theadpool.ThreadPoolConstant;
import com.example.redpackagerain.util.theadpool.ThreadPoolExecutorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ThreadPooService {

    @Resource
    private MessageProducer messageProducer;

    /**
     * 使用线程池执行业务方法并加入视图
     * @param service 要调用的service
     * @param method 被调用的方法
     * @param param 方法参数
     */
    public void runMethod(Object service, Method method, Object... param) {
        threadPoolExecutor().submit(new CustomTask(service, method, param));
    }

    public void submitTask(CustomTask task) {
        threadPoolExecutor().submit(task);
    }

    // 核心线程数：读取当前机器的CPU线程数
    public static int threadsum() {
        return Runtime.getRuntime().availableProcessors();
    }

    //为时间窗口准备一个队列存放元素的集合
    private Queue<Integer> queue = new LinkedList<>();

    public ThreadPoolExecutor getThreadPoolExecutor(){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors() * 2, // 核心线程数: IO类型的任务通常建议设置为CPU核心数的两倍左右
                Runtime.getRuntime().availableProcessors() * 4, // 最大线程数：IO类型的任务建议设置为核心线程数的2-4倍
                60, // 空闲线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new LinkedBlockingQueue<>(10000));
        return executor;
    }

    public ThreadPoolExecutorUtil threadPoolExecutor() {
        return new ThreadPoolExecutorUtil(
                threadsum() * 2, // 核心线程数: IO类型的任务通常建议设置为CPU核心数的两倍左右
                (threadsum() * 2) * 4, // 最大线程数：IO类型的任务建议设置为核心线程数的2-4倍
                60, // 空闲线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new LinkedBlockingQueue<>(10000), // 队列容量
                new RejectedExecutionHandler() {//自定义拒绝策略
                    @Override
                    public void rejectedExecution(Runnable r, java.util.concurrent.ThreadPoolExecutor executor) {
                        log.info("自定义拒绝策略，使用滑动时间窗口和重试纪元");
                        //滑动时间窗口：一段时间内有N条以内数据被拒绝可以进行重试，超过N条就发送告警，用这种形式避免雪上加霜的重试和没有必要的告警。
                        // 举个例子：假设有100万条数据被拒绝了100条，可以直接执行重试机制，因为拒绝的占比非常小，如果100万条数据被拒绝了10万条数据甚至更多，说明线程池参数定义有问题或者有其他性能问题，这是不建议走重试机制，而是进行报警人工干预
                        CustomTask task = (CustomTask)r;
                        log.info("将被拒绝的任务发布消息到消息中间件(或者存入数据库)，然后等待订阅重试，此时重试纪元为" + task.getRetryEpoch());
                        //发送MQ消息（）
                        messageProducer.sendMessageInTransaction(ThreadPoolConstant.REJECTED_TASK, JSONObject.toJSONString(task));
                        int windowSize = 300;//设置时间窗口大小为3
                        long currentTimeMillis = System.currentTimeMillis();//当前时间戳
                        //队列不为空且最早入队的元素超过指定时间窗口大小（单位为秒），则发送告警，进行人工干预并移除元素
                        if (!queue.isEmpty() && currentTimeMillis - queue.peek() > windowSize * 1000) {
                            //使用poll()方法从队列中检索并删除头部元素
                            queue.poll();
                        }
                        //将新元素放到队列尾部
                        queue.offer((int)System.currentTimeMillis());
                    }
                }, this);
    }


    /**
     * TODO 将这些指标发送到MQ中，消费者通过这些生成监控图
     * @param taskCount
     * @param activeCount
     * @param completedTaskCount
     * @param largestPoolSize
     * @param maximumPoolSize
     */
    public void beforeExecute(long taskCount,int activeCount,long completedTaskCount,int largestPoolSize,int maximumPoolSize){
        log.info("当前 task count: {}, active count: {}, completed task count: {}，largestPoolSize：{}, maximumPoolSize：{}",taskCount, activeCount, completedTaskCount, largestPoolSize, maximumPoolSize);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskCount",taskCount);
        jsonObject.put("activeCount",activeCount);
        jsonObject.put("completedTaskCount",completedTaskCount);
        jsonObject.put("largestPoolSize",largestPoolSize);
        jsonObject.put("maximumPoolSize",maximumPoolSize);
        //这里发送事务消息，保证消息不丢失
        messageProducer.sendMessageInTransaction(ThreadPoolConstant.THREADPOOL_BEFOREEXECUTE,jsonObject.toJSONString(),ThreadPoolConstant.TAG_THREADPOOL_PARAMETER);
    }

    /**
     * TODO 待完成，重试被拒绝的任务
     * @param r
     */
    public void threadPooService(Runnable r, BlockingQueue queue) {
        //队列不为空，并且队列长度不大于1000
        if (!queue.isEmpty() && queue.size() < 1000) {
            // 重新获取被拒绝的任务，判断重试纪元是否超过阈值，超过发送mq消息，进行人工干预
            log.info("从数据库或者消息中间件订阅读取被拒绝的任务");
            // TODO 从数据库或者消息中间件订阅读取被拒绝的任务
            CustomTask task = (CustomTask)r;
            log.info("判断任务重试次数，如果超过某个阈值，则触发告警!");
            AtomicInteger retryEpoch = task.getRetryEpoch();
            int currentVal = retryEpoch.get();
            //Task中retryEpoch超过阈值
            if (currentVal > 10) {
                // 发送MQ消息（触发告警进行人工干预）告诉运维人员，某个任务重试次数超过阈值
                messageProducer.sendMessageInTransaction(ThreadPoolConstant.THREADPOOL_AFTEREXECUTE,JSONObject.toJSONString(task),ThreadPoolConstant.TAG_THREADPOOL_TASK);
            }
            log.info("将任务重新添加到线程池");
            this.submitTask(task);
            //将retryEpoch +1
            retryEpoch.incrementAndGet();
        }
    }

    /**
     * beforeExecute方法发送的消息，在这里进行处理，生成监控图
     * @param message
     * @return
     */
    public boolean generateMonitoringInfo(Message message) {
        //TODO 生成监控图
        return true;
    }

    /**
     * threadPooService方法发送的消息，在这里进行处理，进行人工干预
     * @param message
     * @return
     */
    public boolean manualIntervention (Message message) {
        //TODO 发送短信或其他方式进行人工通知
        return true;
    }
}
