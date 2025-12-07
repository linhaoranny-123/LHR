//package com.lhr.gateway.config;
//
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * 网关路由配置类
// * 使用代码方式配置路由规则（也可使用yml配置）
// */
//@Configuration
//public class GatewayConfig {
//
//    @Bean
//    public RouteLocator routes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                // 所有请求都转发到8081，保持原始路径
//                .route("all-requests", r -> r
//                        .path("/**")
//                        .uri("http://localhost:8081")
//                )
//                .build();
//    }
//}