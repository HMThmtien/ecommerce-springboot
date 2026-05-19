package com.example.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Profile("!docker")
public class RedisPingController {

    private final StringRedisTemplate redis;

    @GetMapping("/debug/redis-ping")
    public String ping() {
        redis.opsForValue().set("test:ping", "PONG");
        return redis.opsForValue().get("test:ping");
    }
}

