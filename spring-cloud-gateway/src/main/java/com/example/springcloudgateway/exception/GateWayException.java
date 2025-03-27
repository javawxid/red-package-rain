package com.example.springcloudgateway.exception;

import com.example.springcloudgateway.api.IErrorCode;
import lombok.Data;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 11:19 2022/8/25
 */

@Data
public class GateWayException extends RuntimeException{

    private long code;

    private String message;

    public GateWayException(IErrorCode iErrorCode) {
        this.code = iErrorCode.getCode();
        this.message = iErrorCode.getMessage();
    }
}

