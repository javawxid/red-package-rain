package com.example.redpackagerain.common;


public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),
    RED_PACKAGE_FINISHED(201, "红包抢完了"),
    RED_PACKAGE_REAPT(202, "你已经抢过红包了，不能重复抢"),
    RED_PACKAGE_ERROR(203, "接口流量过大限流或接口异常熔断了"),
    WRONG_USER_RED_PACKET(204, "当前登录用户抢的红包不是该用户的"),
    LOGIN_AUTH(208, "用户未登录");

    private Integer code;      // 业务状态码
    private String message;    // 响应消息

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
