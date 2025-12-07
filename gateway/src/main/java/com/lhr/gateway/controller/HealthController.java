package com.lhr.gateway.controller;  // 确保是这个包

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // 允许跨域访问
public class HealthController {

    @GetMapping("/gateway/health")
    public Mono<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "gateway");
        result.put("port", 8088);
        result.put("timestamp", System.currentTimeMillis());
        result.put("message", "Gateway is working on port 8088");
        return Mono.just(result);
    }

    @GetMapping("/gateway/ping")
    public Mono<String> ping() {
        return Mono.just("pong");
    }
}