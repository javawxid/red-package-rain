package com.example.redpackagerain.model;

import lombok.Data;

import java.util.concurrent.LinkedBlockingQueue;
/**
 * 请求类,code为查询的共同特征,例如查询某表,通过不同id的来区分
 */
@Data
public class RedPackageRainRequest {
    //最大任务数
    public static int MAX_TASK_NUM = 100;
    // 请求id
    public String requestId;
    // 参数
    public String userId;
    public String activityId;
    public String redPackageId;
    // 队列，这个有超时机制
    public LinkedBlockingQueue<RedPackageRainVo> redPackageRainQueue;
}
