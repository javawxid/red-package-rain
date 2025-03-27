package com.yunxi.user.util.theadpool;


import com.yunxi.user.service.ThreadPooService;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolExecutorUtil extends ThreadPoolExecutor {

    private final ThreadPooService threadPooService;

    // 静态队列不应该在这里定义，因为它会在所有实例之间共享，可能导致并发问题
    // 如果需要这样的队列，请考虑在单例服务中管理它
    public ThreadPoolExecutorUtil(
            Integer corePoolSize,
            Integer maximumPoolSize,
            Integer keepAliveTime,
            TimeUnit unit,
            LinkedBlockingQueue<Runnable> workQueue,
            RejectedExecutionHandler rejectedExecutionHandler,
            ThreadPooService threadPooService) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, rejectedExecutionHandler);
        this.threadPooService = threadPooService;
    }

    //为时间窗口准备一个队列存放元素的集合
    private Queue<Integer> queue = new LinkedList<>();

    /**
     * 任务执行前
     * 在每次执行新任务之前调用此方法。
     * 可以在此添加自定义逻辑以监控或诊断线程池状态。
     * @param t 即将执行任务的线程
     * @param r 将要执行的任务
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        // 获取已提交但尚未执行的任务数
        long taskCount = this.getTaskCount();
        // 获取当前正在执行的任务数
        int activeCount = this.getActiveCount();
        // 获取已完成的任务数
        long completedTaskCount = this.getCompletedTaskCount();
        // 获取线程池在生命周期中曾经达到的最大线程数
        int largestPoolSize = this.getLargestPoolSize();
        // 获取线程池的最大线程数
        int maximumPoolSize = this.getMaximumPoolSize();
        // 将这些指标发送到MQ中，消费者通过这些生成监控图
        threadPooService.beforeExecute(taskCount, activeCount, completedTaskCount, largestPoolSize, maximumPoolSize);
    }
    /**
     * 任务执行后
     * @param r
     * @param t
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        threadPooService.threadPooService(r,this.getQueue());
    }
    @Override
    public void shutdown() {
        super.shutdown();
    }


}
