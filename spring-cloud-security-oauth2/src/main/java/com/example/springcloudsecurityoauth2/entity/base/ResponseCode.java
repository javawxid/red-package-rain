package com.example.springcloudsecurityoauth2.entity.base;

/**
 * @author zhiweiLiao
 * @Description 响应码枚举
 * @Date create in 2022/9/29 0029 14:01
 */
public enum ResponseCode {

    SUCCESS(200, "操作成功"),

    TOKEN_EXPIRE(300, "用户权限过期"),

    ILLEGAL_ARGUMENT(400, "错误的请求参数"),

    UNKNOWN(500, "系统异常，请稍后重试");

    private int code;

    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ResponseCode getByCode(int code) {
        for (ResponseCode errorCode : ResponseCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
