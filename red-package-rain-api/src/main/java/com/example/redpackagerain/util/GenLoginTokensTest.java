package com.example.redpackagerain.util;

import java.io.*;
import java.util.UUID;


public class GenLoginTokensTest {


    public static void main(String[] args) {
        for (int i = 1; i <= 3000000; i++) {
            try {
                writeFile("D://opt", "redpackage.txt",UUID.randomUUID().toString());
                writeFile("D://opt", "redpackage.txt", System.getProperty("line.separator"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(String directoryName, String fileName, String content) throws IOException {
        // 创建File对象，表示要写入的目录
        File directory = new File(directoryName);
        // 如果目录不存在，则创建该目录
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 创建File对象，表示要写入的文件
        File file = new File(directory, fileName);
        // 使用try-with-resources语句，自动关闭流
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeFile(String file, String conent) throws IOException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
}