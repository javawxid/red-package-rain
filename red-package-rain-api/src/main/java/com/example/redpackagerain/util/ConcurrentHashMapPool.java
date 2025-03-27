package com.example.redpackagerain.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class ConcurrentHashMapPool {
    private BlockingQueue<ConcurrentHashMap<String, Object>> pool = new LinkedBlockingQueue<>(); // 使用LinkedBlockingQueue实现BlockingQueue
    private final int INIT_CAPACITY = 100; // 初始容量
    private final int MAX_SPIN_ATTEMPTS = 3; // 定义自旋次数
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 用于定期检查服务器性能指标的线程池
    public  ReentrantLock lock = new ReentrantLock();
    public ConcurrentHashMapPool() {
        for (int i = 0; i < 100; i++) {
            ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>(INIT_CAPACITY); // 初始化ConcurrentHashMap对象并添加到对象池
            pool.add(map);
        }
        // 启动一个定期检查服务器性能指标的线程
        //  对象池的动态调整可能存在线程安全问题。在定期检查服务器性能指标的线程中，根据服务器性能指标动态调整对象池大小。如果多个线程同时对对象池进行调整，可能导致并发问题。可以使用同步机制来保证对对象池的修改操作的线程安全性。
        scheduler.scheduleAtFixedRate(() -> {
            int currentCPUUsage = getCurrentCPUUsage(); // 获取当前CPU使用率，根据实际需要实现
            int currentMemoryUsage = getCurrentMemoryUsage(); // 获取当前内存使用率，根据实际需要实现
            // 根据服务器性能指标动态调整对象池大小和最大并发线程数
            if (currentMemoryUsage < 20 || currentCPUUsage < 20) {
                // 内存使用率过低
                addPoolSize();
            }else if (currentMemoryUsage > 80 || currentCPUUsage > 80) {
                // 内存使用率过高，缩小对象池大小或限制对象的创建
                reducePoolSize();
            } else {
                // 其他情况，保持不变或根据需要调整
            }
        }, 1, 60, TimeUnit.SECONDS); // 每60秒检查一次服务器性能指标
    }

    /**
     *  在acquire()方法中，多个线程同时尝试从池中获取对象，可能导致多个线程同时获取到同一个对象。可以考虑使用同步机制（如synchronized关键字或Lock对象）来保证对象池的线程安全性。
     * @return
     */
    public ConcurrentHashMap<String, Object> acquire() {
        lock.lock();
        try {
            ConcurrentHashMap<String, Object> map = null;
            AtomicInteger counter = new AtomicInteger(0);
            while (map == null && counter.incrementAndGet() < MAX_SPIN_ATTEMPTS) { // 尝试自旋获取对象
                try {
                    map = pool.poll(50, TimeUnit.MILLISECONDS); // 每次尝试等待50毫秒
                } catch (InterruptedException e) {
                    // 忽略中断异常，继续自旋
                    log.error(e.getMessage());
                }
            }
            // 如果自旋尝试都失败，则新建一个对象并返回
            if (map == null) {
                map = new ConcurrentHashMap<>(INIT_CAPACITY);
            }
            return map;
        } finally {
            lock.unlock();
        }
    }

    // 在release()方法中，对象释放后直接调用pool.offer()方法将对象放回池中。这种方式可能导致对象池持有过多的对象，如果对象池的大小没有限制，可能会导致内存占用过高。可以考虑设置对象池的最大大小，并在对象池达到最大大小时，释放多余的对象。
    public  void release(ConcurrentHashMap<String, Object> map) {
        // 释放对象资源
        map.clear();
        pool.offer(map);
    }

    // 返回当前CPU使用率
    private int getCurrentCPUUsage() {
        // 获取操作系统的相关信息
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
//        log.info(StringUtil.StringAppend("打印可用的处理器数量:",String.valueOf(osBean.getAvailableProcessors())));
//        log.info(StringUtil.StringAppend("打印操作系统的架构:",osBean.getArch()));
//        log.info(StringUtil.StringAppend("打印操作系统的名称:",osBean.getName()));
//        log.info(StringUtil.StringAppend("打印可用的处理器数量:",String.valueOf(osBean.getAvailableProcessors())));
//        log.info(StringUtil.StringAppend("打印操作系统的系统负载平均值:",String.valueOf(osBean.getSystemLoadAverage())));
        double cpuUsage = osBean.getSystemLoadAverage() / osBean.getAvailableProcessors();
//        log.info("CPU Usage: " + (cpuUsage * 100) + "%");
        return (int) cpuUsage;
    }
    // 返回当前内存使用率
    private int getCurrentMemoryUsage() {
        // 获取当前内存使用情况
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        // 计算内存使用率
        double usedMemory = memoryUsage.getUsed();
        double maxMemory = memoryUsage.getMax();
        double memoryUsagePercent = (usedMemory / maxMemory) * 100;
        // 打印结果
//        log.info("当前内存使用率: " + memoryUsagePercent + "%");
        return (int) memoryUsagePercent; // 示例值，需要根据实际情况计算实际内存使用率并返回值范围（例如：0-100）
    }
    private void addPoolSize() {
        lock.lock();
        try {
            if (pool.size() < 200) {
                for (int i = 0; i < 100; i++) {
                    ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>(INIT_CAPACITY); // 初始化ConcurrentHashMap对象并添加到对象池
                    pool.add(map);
                }
            }
//            log.info(StringUtil.StringAppend("对象池大小：",String.valueOf(pool.size())));
        } finally {
            lock.unlock();
        }
    }
    private void reducePoolSize() {
        lock.lock();
        try {
            // 批量移除对象
            List<ConcurrentHashMap<String, Object>> objectsToRemove = new ArrayList<>();
            pool.drainTo(objectsToRemove, 50);
            // 缩小对象池的大小，移除指定数量的对象
            pool.removeAll(objectsToRemove);
//            log.info(StringUtil.StringAppend("对象池大小：",String.valueOf(pool.size())));
        } finally {
            lock.unlock();
        }
    }

    // 缩小对象池的大小，清理多余的对象
    public void shrinkPool(int newSize) {
        while (pool.size() > newSize) {
            ConcurrentHashMap<String, Object> obj = pool.poll();
            // 释放对象资源
            obj.clear();
        }
    }
}


