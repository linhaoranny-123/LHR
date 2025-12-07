package com.lhr.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局异常处理器
 * 处理网关中的各种异常情况
 */
@Slf4j
@Order(-1)  // 高优先级
@Configuration
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // 设置响应头为JSON格式
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 根据异常类型设置HTTP状态码
        if (ex instanceof NotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return writeErrorResponse(response, 404, "目标服务未找到");
        } else if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatusCode());
            return writeErrorResponse(response,
                    ((ResponseStatusException) ex).getStatusCode().value(),
                    ex.getMessage());
        } else {
            // 其他未知异常
            log.error("网关处理异常: ", ex);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return writeErrorResponse(response, 500, "网关服务内部错误");
        }
    }

    /**
     * 写入错误响应
     */
    private Mono<Void> writeErrorResponse(ServerHttpResponse response, int code, String message) {
        String json = String.format("{\"code\": %d, \"message\": \"%s\", \"timestamp\": %d}",
                code, message, System.currentTimeMillis());

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
}