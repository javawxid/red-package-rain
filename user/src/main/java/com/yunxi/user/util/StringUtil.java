package com.yunxi.user.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class StringUtil {


    /**
     * 多个字符串进行拼接
     * @param str
     * @return
     */
    public static String StringAppend(String... str) {
        StringBuilder sb = new StringBuilder(50);
        for (String s : str) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder(500);
        for (Object obj : list) {
            sb.append(obj.toString());
        }
        return sb.toString();
    }


    /**
     * ConcurrentHashMap转换为字符串
     * @param map
     * @return
     */
    public static String convertToString(ConcurrentHashMap<String, Object> map) {
        StringBuilder sb = new StringBuilder(50);
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 字符串转ConcurrentMap
     * @param str
     * @return
     */
    public static ConcurrentMap<String, Object> stringToMap(String str){
        return Arrays.stream(str.replaceAll("[{} ]", "")
                .split(","))
                .map(s -> s.split("="))
                .collect(Collectors.toConcurrentMap(
                        a -> a[0],
                        a -> (Object) a[1]
                ));
    }

    public static String getCurrentTime(){
        DateTimeFormatter pattern=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return pattern.format(LocalDateTime.now());
    }
}
