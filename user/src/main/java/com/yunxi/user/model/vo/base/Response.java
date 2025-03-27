package com.yunxi.user.model.vo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("统一响应实体类")
public class Response<T> {

    @ApiModelProperty("状态")
    public int status;
    @ApiModelProperty("错误消息")
    public String errMessage;
    @ApiModelProperty("接口数据")
    public T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static Response isSuccess(Object data) {
        Response response = new Response();
        response.setData(data);
        response.setStatus(200);
        return response;
    }

    public static Response isFail(Integer status,String errMessage) {
        Response response = new Response();
        response.setStatus(status);
        response.setErrMessage(errMessage);
        return response;
    }
}
