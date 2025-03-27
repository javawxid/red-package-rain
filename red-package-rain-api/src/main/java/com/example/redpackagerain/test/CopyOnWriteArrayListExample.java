package com.example.redpackagerain.test;

import java.util.concurrent.CopyOnWriteArrayList;
public class CopyOnWriteArrayListExample {
    public static void main(String[] args) {
        // 创建CopyOnWriteArrayList实例
        CopyOnWriteArrayList<Integer> cowList = new CopyOnWriteArrayList<>();
        // 添加元素
        cowList.add(1);
        cowList.add(2);
        cowList.add(3);
        // 读操作示例（无锁）
        for (Integer item : cowList) {
            System.out.println(item);
        }
        // 写操作示例（写时复制）
        cowList.add(4);
        // 打印更新后的数组
        for (Integer item : cowList) {
            System.out.println(item);
        }
    }
}