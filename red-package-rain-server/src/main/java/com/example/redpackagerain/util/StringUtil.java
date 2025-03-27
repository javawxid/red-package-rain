package com.example.redpackagerain.util;

import com.example.redpackagerain.entity.RedPackageRainVo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtil {

        public static void main(String[] args) {
            String originalString = "2024-10-14-16:53:39";
            String[] parts = originalString.split("-");
            if (parts.length >= 4) {
                String modifiedString = parts[0] + "-" + parts[1] + "-" + parts[2] + " " + parts[3];
                System.out.println("修改后的字符串: " + modifiedString);
            } else {
                System.out.println("原始字符串格式不正确");
            }
        }

    public static Date currentTimeToDate(String dateString){
        // 定义日期格式
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String[] parts = dateString.split("-");
            if (parts.length >= 4) {
                String modifiedString = parts[0] + "-" + parts[1] + "-" + parts[2] + " " + parts[3];
                // 将字符串转换为Date对象
                Date date = formatter.parse(modifiedString);
                return date;
            } else {
                System.out.println("原始字符串格式不正确");
            }
        } catch (ParseException e) {
            // 字符串格式不正确时捕获异常
            System.err.println("日期字符串格式不正确：" + e.getMessage());
        }
        return null;
    }

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

    public static ConcurrentMap<String, Object> parseStringToMap(String input) {
        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        String cleanedInput = input.substring(1, input.length() - 1); // Remove surrounding {}
        String[] keyValuePairs = cleanedInput.split("],");
        keyValuePairs[0] = keyValuePairs[0].replace("red_package_list=[","");
        for (int i = 0; i < keyValuePairs.length; i++) {
            if (i == 0) {
                map.putAll(convertToStringToMap(keyValuePairs[i]));
            }else {
                String[]  keyValue = keyValuePairs[i].split(",");
                for (String val : keyValue) {
                    String[] parts = val.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        map.put(key, value);
                    }
                }
            }
        }
        return map;
    }

    public static ConcurrentMap<String, Object> convertToStringToMap(String input) {
        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        List<RedPackageRainVo> list = new ArrayList<>();
        // 正则表达式匹配 RedPackageRainVo 对象 RedPackageRainVo(activityId=33a70fa7f7b04870baac42049280907a, redPackageId=e6ec286b717542b8bb5496f5d324e874, userId=953379309639307264)
        Pattern voPattern = Pattern.compile("RedPackageRainVo\\(activityId=([0-9a-fA-F]+), redPackageId=([0-9a-fA-F]+), userId=([0-9a-fA-F]+)\\)");
        Matcher matcher = voPattern.matcher(input);
        while (matcher.find()) {
            String activityId = String.valueOf(matcher.group(1));
            String redPackageId = String.valueOf(matcher.group(2));
            String userId = String.valueOf(matcher.group(3));
            RedPackageRainVo vo = new RedPackageRainVo(activityId, redPackageId, userId);
            list.add(vo);
        }
        // 将解析得到的列表添加到 map 中
        map.put("list", list);
        return map;
    }

}
