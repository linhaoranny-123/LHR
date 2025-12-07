package com.lhr.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Enumeration;

/**
 * 诊断过滤器，用于记录详细的请求和响应信息
 */
@Slf4j
@Component
public class DiagnosticFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 记录请求信息
        log.info("========== REQUEST INFO ==========");
        log.info("Method: {}", request.getMethod());
        log.info("Path: {}", request.getPath());
        log.info("URI: {}", request.getURI());
        log.info("Query Params: {}", request.getQueryParams());

        // 记录请求头
        HttpHeaders headers = request.getHeaders();
        log.info("Headers:");
        headers.forEach((name, values) -> {
            log.info("  {}: {}", name, values);
        });

        // 记录客户端信息
        log.info("Remote Address: {}", request.getRemoteAddress());
        log.info("Local Address: {}", request.getLocalAddress());

        // 如果是OPTIONS请求，直接返回200
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            log.info("OPTIONS request detected, returning 200");
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "*");
            return exchange.getResponse().setComplete();
        }

        // 添加响应头
        exchange.getResponse().getHeaders().add("X-Gateway-Processed", "true");
        exchange.getResponse().getHeaders().add("X-Gateway-Timestamp", String.valueOf(System.currentTimeMillis()));

        // 继续处理请求
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 记录响应信息
            log.info("========== RESPONSE INFO ==========");
            log.info("Status Code: {}", exchange.getResponse().getStatusCode());
            log.info("Response Headers:");
            exchange.getResponse().getHeaders().forEach((name, values) -> {
                log.info("  {}: {}", name, values);
            });
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}