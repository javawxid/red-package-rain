package com.example.springcloudgateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 根据用户 ID 限流，请求路径中必须携带 userId 参数
 */
@Component
public class UserKeyResolver implements KeyResolver {
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        // 根据请求信息生成唯一的键
        // 例如，从请求头、请求参数或路径中获取用户ID
        String userId = exchange.getRequest().getHeaders().getFirst("userId");
        if (userId == null) {
            // 如果用户ID不存在，可以生成一个默认键或抛出异常
            userId = "username";
        }

        return Mono.just(userId);
    }
}