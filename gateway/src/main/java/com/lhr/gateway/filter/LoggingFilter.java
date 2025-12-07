package com.lhr.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * 自定义日志过滤器
 * 记录请求和响应的基本信息
 */
@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    /**
     * 过滤器配置类（可扩展配置参数）
     */
    public static class Config {
        // 可以在这里添加配置属性，例如是否记录请求体等
    }

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 获取请求信息
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            String requestMethod = request.getMethod().name();
            String remoteAddress = request.getRemoteAddress() != null ?
                    request.getRemoteAddress().getAddress().getHostAddress() : "unknown";

            // 记录请求开始日志
            long startTime = System.currentTimeMillis();
            log.info("请求开始: [{}] {}, 来源IP: {}, 时间: {}",
                    requestMethod, requestPath, remoteAddress, new Date());

            // 打印请求头（可选）
            log.debug("请求头: {}", request.getHeaders());

            // 继续执行过滤器链，并在响应时记录结果
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // 获取响应信息
                var response = exchange.getResponse();
                int statusCode = response.getStatusCode() != null ?
                        response.getStatusCode().value() : 500;
                long duration = System.currentTimeMillis() - startTime;

                // 记录响应日志
                log.info("请求结束: [{}] {}, 状态码: {}, 耗时: {}ms",
                        requestMethod, requestPath, statusCode, duration);
            }));
        };
    }
}