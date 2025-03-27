package com.example.redpackagerain.enums;


public enum ThreadPoolEnum {

    THREADPOOL("threadPool");

    String value = "";

    ThreadPoolEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
