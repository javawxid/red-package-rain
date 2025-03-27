package com.example.springcloudgateway.api;

/**
 * @Author: liaozhiwei
 * @Description: 封装API的错误码
 * @Date: Created in 11:13 2022/8/25
 */
public interface IErrorCode {
    Integer getCode();

    String getMessage();
}


