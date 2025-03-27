package com.example.redpackagerain.util.theadpool;

import com.example.redpackagerain.service.ThreadPooService;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class ThreadPoolExecutorUtil extends ThreadPoolExecutor {

    private final ThreadPooService threadPooService;

    private final AtomicLong taskCount = new AtomicLong(0);

    private final long checkInterval = 10L; // 检查间隔，例如10秒

    private final long taskFrequencyThreshold = 100L; // 任务频率阈值，例如100次/秒

    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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
        // 启动一个定时任务来检查任务频率
        scheduler.scheduleAtFixedRate(() -> {
            long currentTaskCount = taskCount.getAndSet(0); // 获取当前任务数并重置计数
            if (currentTaskCount > taskFrequencyThreshold) {
                System.out.println("任务执行得太频繁！当前频率：" + (currentTaskCount / checkInterval) + "次/秒");
                // 在这里你可以添加你需要的处理逻辑，例如记录日志、发送警告等。
                // 获取已提交但尚未执行的任务数
                long taskCount = this.getTaskCount();
                // 获取当前正在执行的任务数
                int activeCount = this.getActiveCount();
                // 获取已完成的任务数
                long completedTaskCount = this.getCompletedTaskCount();
                // 获取线程池在生命周期中曾经达到的最大线程数
                int largestPoolSize = this.getLargestPoolSize();
                // 将这些指标发送到MQ中，消费者通过这些生成监控图
                threadPooService.beforeExecute(taskCount, activeCount, completedTaskCount, largestPoolSize, maximumPoolSize);
            }
        }, checkInterval, checkInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 任务执行后
     * @param r
     * @param t
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        threadPooService.threadPooService(r,this.getQueue());
        // 每次任务执行完，仅增加任务计数
        taskCount.incrementAndGet();
    }
    @Override
    public void shutdown() {
        super.shutdownNow();
        scheduler.shutdownNow();
    }


}
