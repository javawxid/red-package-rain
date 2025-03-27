package com.example.redpackagerain.model;

import lombok.Data;

@Data
public class RedPackageVo {
    //红包雨活持续时长:单位ms
    private Integer duration;
    //红包生成速率:单位ms
    private Integer generationRate;
    //红包id
    private String redPackageId;
    //活动id
    private String activityId;
    private String createTime;
}
