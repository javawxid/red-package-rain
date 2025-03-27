package com.yunxi.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableAspectJAutoProxy
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
// 开启断路器，开启Hystrix容错能力
@EnableCircuitBreaker
@EnableScheduling
public class UserApplication {

    public static void main(String[] args){
        SpringApplication.run(UserApplication.class, args);
    }

    /*    Ribbon的负载均衡，主要通过LoadBalancerClient来实现的，而LoadBalancerClient具体交给了ILoadBalancer来处理，
    ILoadBalancer通过配置IRule、IPing等信息，并获取注册列表的信息，并默认10秒一次发送“ping”,
    进而检查是否更新服务列表，最后，得到注册列表后，ILoadBalancer根据IRule的策略进行负载均衡。
    而RestTemplate 被@LoadBalance注解后，能过用负载均衡，主要是维护了一个被@LoadBalance注解的RestTemplate列表，
    并给列表中的RestTemplate添加拦截器，进而交给负载均衡器去处理。
    第一种实现方式：RestTemplate 结合@LoadBalanced 以及Ribbon依赖(nacos依赖中已经包含) 使用*/
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
