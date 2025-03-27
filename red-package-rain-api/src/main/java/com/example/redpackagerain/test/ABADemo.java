package com.example.redpackagerain.test;

import java.util.concurrent.atomic.AtomicStampedReference;
public class ABADemo {
    public static void main(String[] args) {
        // 初始值为A，版本号为0
        AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);
        // 获取当前值和版本号
        String current = ref.getReference();
        int stamp = ref.getStamp();
        // 模拟ABA操作
        ref.set("B", stamp + 1); // A -> B，版本号加1
        ref.set("A", stamp + 2); // B -> A，版本号再加1
        // 尝试更新值，预期值为A，预期版本号为0
        boolean success = ref.compareAndSet("A", "C", stamp, stamp + 1);
        System.out.println("Update success: " + success); // 输出false，因为版本号不匹配
    }
}