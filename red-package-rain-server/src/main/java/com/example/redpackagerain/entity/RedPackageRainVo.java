package com.example.redpackagerain.entity;

import lombok.Data;

@Data
public class RedPackageRainVo {

    String activityId;
    String redPackageId;
    String userId;

    public RedPackageRainVo(){

    }
    public RedPackageRainVo(String activityId, String redPackageId, String userId) {
        this.activityId = activityId;
        this.redPackageId = redPackageId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "RedPackageRainVo(activityId=" + activityId + ", redPackageId=" + redPackageId + ", userId=" + userId + ")";
    }
}
