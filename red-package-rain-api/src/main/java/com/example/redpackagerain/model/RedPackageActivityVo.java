package com.example.redpackagerain.model;

import lombok.Data;
import java.util.Date;
@Data
public class RedPackageActivityVo {

    //红包雨活持续时长:单位ms
    private Integer duration;

    //红包生成速率:单位ms
    private Integer generationRate;

    //红包总金额:单位金币
    private Integer totalMoney;

    //红包个数
    private Integer redPackageNumber;

    //红包雨活动开启时间
    private Date date;

    //红包雨活动标识
    private String activityId;
}
