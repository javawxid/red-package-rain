package com.yunxi.user.model.base;

import java.io.Serializable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhiweiLiao
 * @Description 统一返回实体
 * @Date create in 2022/9/29 0029 13:55
 */
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -235826790001701L;

    //状态码
    private Integer code;

    //消息提示
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(hidden = true)
    private String message;

    //返回数据
    private T data;

    public static BaseResponse fail(String mgetMessage) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ResponseCode.UNKNOWN.getCode());
        baseResponse.setMessage(mgetMessage);
        return baseResponse;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> BaseResponse result(T data) {
        BaseResponse baseResponse = new BaseResponse();
        if (data == null){
            baseResponse.setCode(ResponseCode.UNKNOWN.getCode());
            baseResponse.setMessage(ResponseCode.UNKNOWN.getMessage());
            return baseResponse;
        }
        baseResponse.setCode(ResponseCode.SUCCESS.getCode());
        baseResponse.setMessage(ResponseCode.SUCCESS.getMessage());
        baseResponse.setData(data);
        return baseResponse;
    }

    /**
     * 默认构造成功的响应
     */
    public BaseResponse() {
        this.setCode(ResponseCode.SUCCESS.getCode());
        this.setMessage(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 自身内容能以可读方式输出
     * @return
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":"
                + JSON.toJSONString(this, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.SkipTransientField);
    }


}
