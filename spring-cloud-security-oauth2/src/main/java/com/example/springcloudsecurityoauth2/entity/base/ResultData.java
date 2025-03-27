package com.example.springcloudsecurityoauth2.entity.base;

/**
 * @Author: liaozhiwei
 * @Description: TODO
 * @Date: Created in 18:34 2022/8/23
 */

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class ResultData<T> implements Serializable {
    /**
     * 状态码
     */
    public boolean status = true;

    /**
     * 状态码
     */
    private Integer code = 200;

    /**
     * 接口返回信息
     */
    private String msg;

    /**
     * 数据对象
     */
    private T data;

    /**
     * 初始化一个新创建的 ResultData 对象
     *
     * @param status 状态码
     * @param msg    返回内容
     */
    public ResultData(Boolean status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**
     * 初始化一个新创建的 ResultData 对象
     *
     * @param status 状态码
     * @param msg    返回内容
     * @param data   数据对象
     */
    public ResultData(Boolean status, String msg, T data, Integer code) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.code = code;
    }

    public ResultData(T data) {
        this.data = data;
    }


    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> ResultData<T> success(String msg, T data) {
        return new ResultData<T>(true, msg, data, 200);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static <T> ResultData<T> success(String msg) {
        return ResultData.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static <T> ResultData<T> success() {
        return ResultData.success(null);
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> ResultData<T> success(T data) {
        return ResultData.success(null, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static <T> ResultData<T> error() {
        return ResultData.error(null);
    }


    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> ResultData<T> error(String msg) {
        return ResultData.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static <T> ResultData<T> error(Integer code, String msg) {
        return new ResultData<T>(false, msg, null, code);
    }


    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> ResultData<T> error(String msg, T data) {
        return new ResultData<T>(false, msg, data, 500);
    }
}

