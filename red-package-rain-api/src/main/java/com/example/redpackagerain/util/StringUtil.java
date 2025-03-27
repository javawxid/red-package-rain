package com.example.redpackagerain.util;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.redpackagerain.constant.RedPackageRainConstant;
import com.example.redpackagerain.model.RedPackageRainVo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 消息中间件用字符串推送会将空格去掉，这里临时用-顶替，后续消费端拿到消息再做处理即可 todo 这种格式不太友好
     * @return
     */
    public static String getCurrentTimeDate(){
        LocalDateTime now = LocalDateTime.now();
        return DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss").format(now);
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

    public static String ObjectsToString(Object... param) {
        return JSONObject.toJSONString(param);
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

    public static void main(String[] args) {
        List<RedPackageRainVo> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            RedPackageRainVo redPackageRainVo =  new RedPackageRainVo();
            redPackageRainVo.setRedPackageId(String.valueOf(i));
            redPackageRainVo.setActivityId(String.valueOf(i));
            redPackageRainVo.setUserId(String.valueOf(i));
            list.add(redPackageRainVo);
        }
        ConcurrentHashMap concurrentHashMap =  new ConcurrentHashMap(); // 从对象池中获取一个ConcurrentHashMap实例
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_LIST,list);
        concurrentHashMap.put(RedPackageRainConstant.RED_PACKAGE_MESSAGE_ID,IdUtil.simpleUUID().replace("-", ""));
        //将ConcurrentHashMap对象转换成字符串。
        String convertToString = StringUtil.convertToString(concurrentHashMap);
        ConcurrentMap<String, Object> result = parseStringToMap(convertToString);
        System.out.println(result);
    }

    public static ConcurrentMap<String, Object> parseStringToMap(String input) {
        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        String cleanedInput = input.substring(1, input.length() - 1); // Remove surrounding {}
        String[] keyValuePairs = cleanedInput.split("],");
        keyValuePairs[0] = keyValuePairs[0].replace("red_package_list=[","");
        map.putAll(convertToStringToMap(keyValuePairs[0]));
        String[] parts = keyValuePairs[1].split("=", 2);
        if (parts.length == 2) {
            String key = parts[0].trim();
            String value = parts[1].trim();
            map.put(key, value);
        }
        return map;
    }

    public static ConcurrentMap<String, Object> convertToStringToMap(String input) {
        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        List<RedPackageRainVo> list = new ArrayList<>();
        // 正则表达式匹配 RedPackageRainVo 对象
        Pattern voPattern = Pattern.compile("RedPackageRainVo\\(activityId=(\\d+), redPackageId=(\\d+), userId=(\\d+)\\)");
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
