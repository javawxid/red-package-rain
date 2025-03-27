package com.yunxi.user.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import com.yunxi.user.feign.RedPacketredPacketFeign;
import com.yunxi.user.model.base.BaseResponse;
import com.yunxi.user.model.vo.req.user.ChooseSpouseVO;
import com.yunxi.user.model.vo.req.user.GetUserInfoReqVO;
import com.yunxi.user.model.vo.req.user.SaveUserInfoReqVO;
import com.yunxi.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhiweiLiao
 * @Description 第一版使用Hystrix，可以发现每个接口上面都需要添加一系列注解，过于繁琐
 * @Date create in 2022/9/29 0029 13:34
 */

@Api(description = "用户管理")
@RestController
@RequestMapping("/userinfo")
@DefaultProperties(defaultFallback = "fallbackMethod")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    RedPacketredPacketFeign redPacketredPacketFeign;



    @HystrixCommand(
            commandProperties = {
                    // 超时时间，默认1000ms
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "2000"),
                    // 信号量隔离
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"),
                    // 信号量最大并发
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "100"),
                    // 请求数阈值：在快照时间窗口内，必须满足请求阈值数才有资格熔断。打开断路器的最少请求数，默认20个请求。
                    //意味着在时间窗口内，如果调用次数少于20次，即使所有的请求都超时或者失败，断路器都不会打开
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "10"),
                    // 错误百分比阈值：当请求总数在快照内超过了阈值，且有一半的请求失败，这时断路器将会打开。默认50%
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "75"),
                    // 快照时间窗口：断路器开启时需要统计一些请求和错误数据，统计的时间范围就是快照时间窗口，默认5秒
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "10000")
            },
            groupKey = "userService", // 服务名称，相同名称使用同一个线程池
            commandKey = "getUserInfo",// 接口名称，默认为方法名
            threadPoolKey = "userServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },fallbackMethod = "getUserInfoFallbackMethod"
    )
    @ApiOperation(value="获取用户信息")
    @PostMapping("/getUserInfo")
    public BaseResponse getUserInfo(@RequestBody GetUserInfoReqVO reqVO){
        return BaseResponse.result(userService.getUserInfo(reqVO));
    }

    private BaseResponse getUserInfoFallbackMethod(GetUserInfoReqVO reqVO) {
        return BaseResponse.fail("用户获取用户信息方法执行失败啦，入参：" + reqVO.toString());
    }


    @HystrixCommand(
            commandProperties = {
                    // 超时时间，默认1000ms
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "2000"),
                    // 信号量隔离
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"),
                    // 信号量最大并发
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "100"),
                    // 请求数阈值：在快照时间窗口内，必须满足请求阈值数才有资格熔断。打开断路器的最少请求数，默认20个请求。
                    //意味着在时间窗口内，如果调用次数少于20次，即使所有的请求都超时或者失败，断路器都不会打开
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "10"),
                    // 错误百分比阈值：当请求总数在快照内超过了阈值，且有一半的请求失败，这时断路器将会打开。默认50%
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "75"),
                    // 快照时间窗口：断路器开启时需要统计一些请求和错误数据，统计的时间范围就是快照时间窗口，默认5秒
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "10000")
            },
            groupKey = "userService", // 服务名称，相同名称使用同一个线程池
            commandKey = "saveUserInfo",// 接口名称，默认为方法名
            threadPoolKey = "userServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },
            fallbackMethod = "saveUserInfoFallbackMethod"
    )
    @ApiOperation(value="保存或者更新用户信息")
    @PostMapping("/saveUserInfo")
    public BaseResponse saveUserInfo(@RequestBody SaveUserInfoReqVO reqVO){
        return BaseResponse.result(userService.saveUserInfo(reqVO));
    }

    private BaseResponse saveUserInfoFallbackMethod(SaveUserInfoReqVO reqVO) {
        return BaseResponse.fail("保存或者更新用户信息方法执行失败啦，入参：" + reqVO.toString());
    }


    @HystrixCommand(
            commandProperties = {
                    // 超时时间，默认1000ms
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "2000"),
                    // 信号量隔离
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"),
                    // 信号量最大并发
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "100"),
                    // 请求数阈值：在快照时间窗口内，必须满足请求阈值数才有资格熔断。打开断路器的最少请求数，默认20个请求。
                    //意味着在时间窗口内，如果调用次数少于20次，即使所有的请求都超时或者失败，断路器都不会打开
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "10"),
                    // 错误百分比阈值：当请求总数在快照内超过了阈值，且有一半的请求失败，这时断路器将会打开。默认50%
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "75"),
                    // 快照时间窗口：断路器开启时需要统计一些请求和错误数据，统计的时间范围就是快照时间窗口，默认5秒
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "10000")
            },
            groupKey = "userService", // 服务名称，相同名称使用同一个线程池
            commandKey = "chooseSpouse",// 接口名称，默认为方法名
            threadPoolKey = "userServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },
            fallbackMethod = "chooseSpouseFallbackMethod"
    )
    @ApiOperation(value="保存择偶标准")
    @PostMapping("/chooseSpouse")
    public BaseResponse chooseSpouse(@RequestBody ChooseSpouseVO reqVO){
        return BaseResponse.result(userService.chooseSpouse(reqVO));
    }

    private BaseResponse chooseSpouseFallbackMethod(ChooseSpouseVO reqVO) {
        return BaseResponse.fail("保存择偶标准方法执行失败啦，入参：" + reqVO.toString());
    }

    @HystrixCommand(
            commandProperties = {
                    // 超时时间，默认1000ms
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "2000"),
                    // 信号量隔离
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_STRATEGY, value = "SEMAPHORE"),
                    // 信号量最大并发
                    @HystrixProperty(name = HystrixPropertiesManager.
                            EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "10000"),
                    // 请求数阈值：在快照时间窗口内，必须满足请求阈值数才有资格熔断。打开断路器的最少请求数，默认20个请求。
                    //意味着在时间窗口内，如果调用次数少于20次，即使所有的请求都超时或者失败，断路器都不会打开
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "10"),
                    // 错误百分比阈值：当请求总数在快照内超过了阈值，且有一半的请求失败，这时断路器将会打开。默认50%
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "75"),
                    // 快照时间窗口：断路器开启时需要统计一些请求和错误数据，统计的时间范围就是快照时间窗口，默认5秒
                    @HystrixProperty(name = HystrixPropertiesManager.
                            CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "10000")
            },
            groupKey = "redPacketredPacketFeign", // 服务名称，相同名称使用同一个线程池
            commandKey = "grabRedPacket",// 接口名称，默认为方法名
            threadPoolKey = "redPacketredPacketFeignPool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "1000"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },
            fallbackMethod = "saveUserInfoFallbackMethod"
    )
    @ApiOperation(value="保存或者更新用户信息")
    @PostMapping("/test")
    public BaseResponse test(){
        return BaseResponse.result(redPacketredPacketFeign.grabRedPacket());
    }

    private BaseResponse fallbackMethod(Object reqVO) {
        return BaseResponse.fail("方法执行失败啦，入参：" + reqVO.toString());
    }

    @GetMapping("/getUserinfoById/{userId}")
    public BaseResponse getUserinfoById(@PathVariable("userId") String userId){
        return BaseResponse.result(userService.getUserinfoById(userId));
    }

    @GetMapping("/getUserByUserName/{userName}")
    public BaseResponse getUserByUserName(@PathVariable("userName") String userName){
        return BaseResponse.result(userService.getUserByUserName(userName));
    }

    /**
     * 写入脚本
     * @param offset
     * @param pageSize
     */
    @GetMapping(value = "/writeScriptFile")
    public void writeScriptFile(Long offset,Long pageSize) {
        userService.writeScriptFile(offset,pageSize);
    }

    @GetMapping(value = "/startCreateUserTask")
    public void startCreateUserTask(Long num){
        userService.startCreateUserTask(num);
    }

    @GetMapping(value = "/stopCreateUserTask")
    public void stopCreateUserTask(){
        userService.stopCreateUserTask();
    }
}
