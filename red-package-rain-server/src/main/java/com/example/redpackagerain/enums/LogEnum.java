package com.example.redpackagerain.enums;


public enum LogEnum {

    ROBREDPACKAGELOG("robRedPackageLog");

    String value = "";

    LogEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
