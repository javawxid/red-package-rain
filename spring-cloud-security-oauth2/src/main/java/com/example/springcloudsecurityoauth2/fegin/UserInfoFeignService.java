package com.example.springcloudsecurityoauth2.fegin;

import com.example.springcloudsecurityoauth2.entity.base.BaseResponse;
import com.example.springcloudsecurityoauth2.hystrix.UserServiceHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: liaozhiwei
 * @Description: 远程调用用户模块的接口获取用户信息
 * @Date: Created in 18:33 2022/8/23
 */
@Component
@FeignClient(name = "user", fallback = UserServiceHystrix.class, path = "/")
public interface UserInfoFeignService {

    @GetMapping("/userinfo/getUserinfoById/{userId}")
    BaseResponse getUserinfoById(@PathVariable("userId") String userId);

    @GetMapping("/userinfo/getUserByUserName/{userName}")
    BaseResponse getUserByUserName(@PathVariable("userName") String userName);

}
