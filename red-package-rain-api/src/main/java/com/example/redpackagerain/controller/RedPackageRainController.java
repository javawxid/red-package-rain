package com.example.redpackagerain.controller;

import com.example.redpackagerain.common.Result;
import com.example.redpackagerain.common.ResultCodeEnum;
import com.example.redpackagerain.model.RedPackageActivityVo;
import com.example.redpackagerain.model.RedPackageRainVo;
import com.example.redpackagerain.model.RedPackageVo;
import com.example.redpackagerain.service.RedPackageRainServcie;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/redPackageRainApi")
public class RedPackageRainController {

    @Autowired
    private RedPackageRainServcie redPackageRainServcie;

    //高并发抢红包
    @Retry(name = "retryApi",fallbackMethod = "fallbackRedPackage")
    @CircuitBreaker(name = "circuitBreakerApi",fallbackMethod = "fallbackRedPackage")
    @RateLimiter(name = "rateLimiterApi",fallbackMethod = "fallbackRedPackage")
    @PostMapping(value = "/snatchRedPackage")
    public Result snatchRedPackage(@RequestBody RedPackageRainVo redPackage, @RequestHeader("userId") String userId) throws Exception{
        return redPackageRainServcie.snatchRedPackage(redPackage,userId);
    }

    //高并发请求合并抢红包
    @Retry(name = "retryApi",fallbackMethod = "fallbackRedPackage")
    @CircuitBreaker(name = "circuitBreakerApi",fallbackMethod = "fallbackRedPackage")
    @RateLimiter(name = "rateLimiterApi",fallbackMethod = "fallbackRedPackage")
    @PostMapping(value = "/snatchRedPackageMergeRequest")
    public Result snatchRedPackageMergeRequest(@RequestBody RedPackageRainVo redPackage, @RequestHeader("userId") String userId){
        return Result.success(redPackageRainServcie.snatchRedPackageMergeRequest(redPackage,userId));
    }

    public Result fallbackRedPackage(Throwable throwable) {
        log.error("fallback RedPackage info:",throwable.getMessage());
        return Result.error(ResultCodeEnum.RED_PACKAGE_ERROR.getCode(),ResultCodeEnum.RED_PACKAGE_ERROR.getMessage());
    }

    /**
     * 添加活动
     * @param req
     * @return
     */
    @PostMapping(value = "/addRedPackageActivity")
    public Result<RedPackageVo> addRedPackageActivity(@RequestBody RedPackageActivityVo req) {
        return redPackageRainServcie.addRedPackageActivity(req);
    }

    /**
     * 活动列表
     * @return
     */
    @Retry(name = "retryApi",fallbackMethod = "fallbackRedPackage")
    @CircuitBreaker(name = "circuitBreakerApi",fallbackMethod = "fallbackRedPackage")
    @RateLimiter(name = "rateLimiterApi",fallbackMethod = "fallbackRedPackage")
    @GetMapping(value = "/listRedPackage")
    public Result<List<RedPackageActivityVo>> listRedPackage() {
        return redPackageRainServcie.listRedPackage();
    }


    /**
     * 领取记录
     * @param redPackageId
     * @param userId
     * @return
     */
    @Retry(name = "retryApi",fallbackMethod = "fallbackRedPackage")
    @CircuitBreaker(name = "circuitBreakerApi",fallbackMethod = "fallbackRedPackage")
    @RateLimiter(name = "rateLimiterApi",fallbackMethod = "fallbackRedPackage")
    @GetMapping(value = "/record/{redPackageId}")
    public Result<Integer> record(@PathVariable String redPackageId,@RequestHeader("userId") String userId) {
        return redPackageRainServcie.record(redPackageId,userId);
    }

}

