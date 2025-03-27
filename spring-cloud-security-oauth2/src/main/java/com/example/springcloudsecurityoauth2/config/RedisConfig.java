package com.example.springcloudsecurityoauth2.config;

/**
 * @Description 设置的json序列化方式进行存储，我们也可以在redis中查看内容的时候方便的查看到属性值。
 * 单机版配置
 */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;

@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 重写key的生成策略，用【类名+方法名+参数名】这样就可以保证key不为空
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName()).append(".").append(method.getName());
            if (objects != null && objects.length > 0) {
                sb.append("::").append(Arrays.deepToString(objects));
            }
            return sb.toString();
        };
    }

    @Bean
    //"unchecked": 当你在代码中执行了某些未检查的操作时，比如使用了泛型时没有指定具体的类型参数，编译器会发出这个警告。未检查的操作可能导致运行时异常，因为编译器在编译时不能确保类型安全。
    // "rawtypes": 当你在使用泛型时未指定类型参数，例如直接使用List而不是List<String>时，编译器会发出这个警告。这个警告通常意味着你可能失去了泛型提供的类型安全优势。
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 自定义的Jackson2JsonRedisSerializer
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置可见性，以便序列化私有字段
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用默认的类型信息，这对于反序列化是必要的
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        // 设置value的序列化器
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置Hash的value序列化器
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 设置key的序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 设置Hash的key序列化器
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 初始化RedisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}


