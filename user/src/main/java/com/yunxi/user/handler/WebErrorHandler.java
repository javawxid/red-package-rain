package com.yunxi.user.handler;

import com.yunxi.user.model.base.BaseResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhiweiLiao
 * @Description Controller层外的异常(框架层)，或者是@ExceptionHandler遗漏的异常，可以通过自定义ErrorController进行统一拦截处理
 * @Date create in 2022/9/30 0030 9:45
 */
@RestController
public class WebErrorHandler implements ErrorController {

    private static final String ERROR_PATH = "/error";

    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * ErrorController可对全局错误进行处理，但是其获取不到异常的具体信息，不能直接判断异常类型，但是可以通过如下方式进行异常判断。
     * @param request
     * @return
     */
    @RequestMapping("/error")
    public BaseResponse error(HttpServletRequest request) {
        if(request.getAttribute("javax.servlet.error.exception") != null){
            String msg = "";
            Exception exception = (Exception)request.getAttribute("javax.servlet.error.exception");
            switch (exception.getCause().getClass().getName()){
                case "java.lang.NullPointerException" :
                    msg = "最外层捕获的空指针异常";
                    break;
                // TODO 这里可以添加其他异常类型
                default:
                    msg = exception.getCause().getMessage();
            }
            return BaseResponse.fail(msg);
        }
        return BaseResponse.fail("最外层的异常");
    }
}
