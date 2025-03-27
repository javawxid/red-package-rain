package com.example.redpackagerain.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
public class LSMTree {
    // WAL (Write-Ahead Log) 用于记录所有写操作的日志
    private List<String> wal;
    // MemTable 用于存储当前写入的数据
    private ConcurrentSkipListMap<String, String> memTable;
    // Immutable MemTable 用于存储即将写入SSTable的不可变数据
    private ConcurrentSkipListMap<String, String> immutableMemTable;
    // SSTables (Sorted String Tables) 用于存储持久化的数据
    private List<ConcurrentSkipListMap<String, String>> sstables;
    // 容量限制，简化处理使用计数作为代理
    private final int MEMTABLE_CAPACITY = 32; // in MB
    public LSMTree() {
        wal = new ArrayList<>(); // 初始化WAL
        memTable = new ConcurrentSkipListMap<>(); // 初始化MemTable
        immutableMemTable = new ConcurrentSkipListMap<>(); // 初始化Immutable MemTable
        sstables = new ArrayList<>(); // 初始化SSTables列表
    }
    // 写操作
    public void put(String key, String value) {
        // 写入WAL
        String logEntry = "PUT " + key + " " + value;
        wal.add(logEntry);
        // 写入MemTable
        memTable.put(key, value);
        // 检查MemTable是否需要转换为Immutable MemTable
        if (memTable.size() >= MEMTABLE_CAPACITY) {
            convertMemTableToImmutable();
        }
    }
    // 将MemTable转换为Immutable MemTable
    private synchronized void convertMemTableToImmutable() {
        immutableMemTable = memTable; // 将当前MemTable设置为Immutable
        memTable = new ConcurrentSkipListMap<>(); // 创建新的MemTable
        // 触发后台进程将Immutable MemTable刷新到SSTable
        new Thread(this::flushImmutableMemTableToSSTable).start();
    }
    // 将Immutable MemTable刷新到SSTable
    private void flushImmutableMemTableToSSTable() {
        ConcurrentSkipListMap<String, String> newSSTable = immutableMemTable; // 获取Immutable MemTable
        sstables.add(newSSTable); // 添加到SSTables列表
        immutableMemTable = new ConcurrentSkipListMap<>(); // 重置Immutable MemTable
        // 如果需要，触发压缩操作
        if (sstables.size() > 1) {
            compactSSTables();
        }
    }
    // 压缩SSTables
    private void compactSSTables() {
        // 简化的压缩逻辑
        ConcurrentSkipListMap<String, String> newSSTable = new ConcurrentSkipListMap<>();
        for (ConcurrentSkipListMap<String, String> sstable : sstables) {
            newSSTable.putAll(sstable); // 合并所有SSTable
        }
        sstables.clear(); // 清空旧的SSTables
        sstables.add(newSSTable); // 添加新的压缩后的SSTable
    }
    // 读操作
    public String get(String key) {
        // 检查MemTable
        if (memTable.containsKey(key)) {
            return memTable.get(key);
        }
        // 检查Immutable MemTable
        if (immutableMemTable.containsKey(key)) {
            return immutableMemTable.get(key);
        }
        // 检查SSTables
        for (ConcurrentSkipListMap<String, String> sstable : sstables) {
            if (sstable.containsKey(key)) {
                return sstable.get(key);
            }
        }
        return null; // 未找到键
    }
    public static void main(String[] args) {
        LSMTree lsmTree = new LSMTree();
        lsmTree.put("foo", "bar"); // 写入键值对 foo -> bar
        lsmTree.put("baz", "qux"); // 写入键值对 baz -> qux
        System.out.println("Get foo: " + lsmTree.get("foo")); // 读取键 foo
        System.out.println("Get baz: " + lsmTree.get("baz")); // 读取键 baz
        System.out.println("Get non-existent key: " + lsmTree.get("nonexistent")); // 读取不存在的键
    }
}