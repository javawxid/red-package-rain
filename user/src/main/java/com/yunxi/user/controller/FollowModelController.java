package com.yunxi.user.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import com.yunxi.user.model.base.BaseResponse;
import com.yunxi.user.model.vo.req.followModel.FollowReqVO;
import com.yunxi.user.model.vo.req.followModel.MightKnowReqVO;
import com.yunxi.user.model.vo.req.followModel.RecentVisitsReqVO;
import com.yunxi.user.model.vo.req.followModel.TogetherReqVO;
import com.yunxi.user.model.vo.req.user.GetFollowListReqVO;
import com.yunxi.user.service.FollowModelService;
import com.yunxi.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhiweiLiao
 * @Description 第一版使用Hystrix，可以发现每个接口上面都需要添加一系列注解，过于繁琐，后期改用流控组件
 * @Date create in 2022/9/29 0029 13:34
 */

@Api(description = "用户管理")
@RestController
@RequestMapping("/follow/model")
@DefaultProperties(defaultFallback = "fallbackMethod")
public class FollowModelController {

    @Autowired
    private FollowModelService followModelService;

    @Autowired
    private UserService userService;


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
            groupKey = "followModelService", // 服务名称，相同名称使用同一个线程池
            commandKey = "getFollowList",// 接口名称，默认为方法名
            threadPoolKey = "followModelServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },fallbackMethod = "getFollowListFallbackMethod"
    )
    @ApiOperation(value="获取关注/被关注的人列表")
    @GetMapping("/getFollowList")
    public BaseResponse getFollowList(@RequestBody GetFollowListReqVO reqVO){
        return BaseResponse.result(followModelService.getFollowList(reqVO));
    }

    private BaseResponse getFollowListFallbackMethod(GetFollowListReqVO reqVO) {
        return BaseResponse.fail("获取关注/被关注的人列表执行失败啦，入参：" + reqVO.toString());
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
            groupKey = "followModelService", // 服务名称，相同名称使用同一个线程池
            commandKey = "recentVisits",// 接口名称，默认为方法名
            threadPoolKey = "followModelServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },fallbackMethod = "recentVisitsFallbackMethod"
    )
    @ApiOperation(value="近期访问")
    @GetMapping("/recentVisits")
    public BaseResponse recentVisits(@RequestBody RecentVisitsReqVO reqVO){
        return BaseResponse.result(followModelService.recentVisits(reqVO));
    }

    private BaseResponse recentVisitsFallbackMethod(RecentVisitsReqVO reqVO) {
        return BaseResponse.fail("近期访问执行失败啦，入参：" + reqVO.toString());
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
            groupKey = "followModelService", // 服务名称，相同名称使用同一个线程池
            commandKey = "mightKnow",// 接口名称，默认为方法名
            threadPoolKey = "followModelServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },fallbackMethod = "mightKnowFallbackMethod"
    )
    @ApiOperation(value="我可能认识的人")
    @GetMapping("/mightKnow")
    public BaseResponse mightKnow(@RequestBody MightKnowReqVO reqVO){
        return BaseResponse.result(followModelService.mightKnow(reqVO));
    }

    private BaseResponse mightKnowFallbackMethod(MightKnowReqVO reqVO) {
        return BaseResponse.fail("我可能认识的人执行失败啦，入参：" + reqVO.toString());
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
            groupKey = "followModelService", // 服务名称，相同名称使用同一个线程池
            commandKey = "together",// 接口名称，默认为方法名
            threadPoolKey = "followModelServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },fallbackMethod = "togetherFallbackMethod"
    )
    @ApiOperation(value="共同关注的人")
    @GetMapping("/together")
    public BaseResponse together(@RequestBody TogetherReqVO reqVO){
        return BaseResponse.result(followModelService.together(reqVO));
    }

    private BaseResponse togetherFallbackMethod(TogetherReqVO reqVO) {
        return BaseResponse.fail("共同关注的人执行失败啦，入参：" + reqVO.toString());
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
            groupKey = "followModelService", // 服务名称，相同名称使用同一个线程池
            commandKey = "followedFollowed",// 接口名称，默认为方法名
            threadPoolKey = "followModelServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },fallbackMethod = "followedFollowedFallbackMethod"
    )
    @ApiOperation(value="我关注的人也关注了他")
    @GetMapping("/followedFollowed")
    public BaseResponse followedFollowed(@RequestBody TogetherReqVO reqVO){
        return BaseResponse.result(followModelService.followedFollowed(reqVO));
    }

    private BaseResponse followedFollowedFallbackMethod(TogetherReqVO reqVO) {
        return BaseResponse.fail("我关注的人也关注了他执行失败啦，入参：" + reqVO.toString());
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
            groupKey = "followModelService", // 服务名称，相同名称使用同一个线程池
            commandKey = "follow",// 接口名称，默认为方法名
            threadPoolKey = "followModelServicePool",//线程池名称，相同名称使用同一个线程池
            threadPoolProperties = {
                    // 并发执行的核心线程数，默认10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "100"),
                    // 等待队列长度（最大队列长度，默认值-1）BlockingQueue的最大队列数 默认600
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "800"),
                    // 线程空闲存活时间，默认1min
                    @HystrixProperty(name = HystrixPropertiesManager.KEEP_ALIVE_TIME_MINUTES, value = "3"),
                    // 超出等待队列阈值执行拒绝策略
                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "800")
            },fallbackMethod = "followFallbackMethod"
    )
    @ApiOperation(value="关注")
    @GetMapping("/follow")
    public BaseResponse follow(@RequestBody FollowReqVO reqVO){
        return BaseResponse.result(followModelService.follow(reqVO));
    }

    private BaseResponse followFallbackMethod(FollowReqVO reqVO) {
        return BaseResponse.fail("关注执行失败啦，入参：" + reqVO.toString());
    }


    private BaseResponse fallbackMethod(Object reqVO) {
        return BaseResponse.fail("方法执行失败啦，入参：" + reqVO.toString());
    }


}
