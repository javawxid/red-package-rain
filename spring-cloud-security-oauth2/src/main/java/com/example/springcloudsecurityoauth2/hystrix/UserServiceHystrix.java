package com.example.springcloudsecurityoauth2.hystrix;


import com.example.springcloudsecurityoauth2.entity.base.BaseResponse;
import com.example.springcloudsecurityoauth2.fegin.UserInfoFeignService;
import org.springframework.stereotype.Component;

/**
 * @author zhiwei Liao
 * @version 1.0
 * @Description
 * @Date 2021/8/17 15:25
 */

@Component
public class UserServiceHystrix implements UserInfoFeignService {


    @Override
    public BaseResponse getUserinfoById(String userId) {
        return BaseResponse.fail("接口处理异常");
    }

    @Override
    public BaseResponse getUserByUserName(String username) {
        return BaseResponse.fail("接口处理异常");
    }
}

