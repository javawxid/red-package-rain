package com.example.redpackagerain.controller;

import com.example.redpackagerain.test.ratelimiter.TokenBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TokenBucketController {
    @Autowired
    private TokenBucket tokenBucket;
    @GetMapping("/consume")
    public String consume(@RequestParam String key, @RequestParam int tokens) {
        if (tokenBucket.tryConsume(key, tokens)) {
            return "Tokens consumed successfully";
        } else {
            return "Not enough tokens";
        }
    }
}