package com.example.redpackagerain.strategy;

/**
 * @Description: 准备使用反射
 */
public enum StrategyEnum {

    DoubleMeanStrategy("com.example.redpackagerain.strategy.DoubleMeanStrategy"),
    SegmentCuttingStrategy("com.example.redpackagerain.strategy.SegmentCuttingStrategy"),
    RedPackageActivity("com.example.redpackagerain.strategy.RedPackageActivity"),
    RedPackageActivityMergeRequest("com.example.redpackagerain.strategy.RedPackageActivityMergeRequest"),
    ;

    String value = "";

    StrategyEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
