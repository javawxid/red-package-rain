package com.example.redpackagerain.common;

import lombok.Data;

@Data
public class Result<T> {

    //返回码
    private Integer code;

    //返回消息
    private String message;

    //返回数据
    private T data;

    // 私有化构造
    public Result() {
    }

    // 返回数据
    public static <T> Result<T> build(T body, Integer code, String message) {
        Result<T> result = new Result<>();
        result.setData(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    // 返回数据
    public static <T> Result<T> success(T body) {
        Result<T> result = new Result<>();
        result.setData(body);
        result.setCode(200);
        return result;
    }

    // 通过枚举构造Result对象
    public static <T> Result build(T body, ResultCodeEnum resultCodeEnum) {
        return build(body, resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }
    public static <T> Result error(Integer code,String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

}
