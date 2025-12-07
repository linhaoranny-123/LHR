package com.lhr.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lhr
 * @additional_information
 */
@Component
public class DownloadResponseHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();

            // 检查是否是下载请求
            String path = exchange.getRequest().getURI().getPath();
            String query = exchange.getRequest().getURI().getQuery();

            if ((path != null && path.contains("/download")) ||
                    (query != null && query.contains("download"))) {

                // 确保 Content-Disposition 头不被移除
                if (headers.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
                    // 如果后端已经设置了，确保网关不会移除它
                    // 可以在这里添加日志
                    System.out.println("Download header present: " +
                            headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
                } else {
                    // 如果后端没有设置，网关可以添加
                    // 但最好让后端处理
                }
            }
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}