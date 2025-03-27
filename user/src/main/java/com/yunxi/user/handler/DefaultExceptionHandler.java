package com.yunxi.user.handler;

import com.yunxi.user.model.base.BaseResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhiweiLiao
 * @Description 处理Controller中未捕获的异常,将异常以json格式返回给客户端
 * @Date create in 2022/9/30 0030 9:41
 */

@ControllerAdvice
@ResponseBody
public class DefaultExceptionHandler {

    // 捕获Controller抛出的空指针异常
    @ExceptionHandler(NullPointerException.class)
    public BaseResponse handlerNullException() {
        // 封装异常信息为RespinseResult对象
        return BaseResponse.fail("空指针异常");
    }

    // 兜底异常，处理所有代码中未考虑到的异常
    @ExceptionHandler(Exception.class)
    public BaseResponse handlerException() {
        // 封装异常信息为RespinseResult对象
        return BaseResponse.fail("处理异常了");
    }

}