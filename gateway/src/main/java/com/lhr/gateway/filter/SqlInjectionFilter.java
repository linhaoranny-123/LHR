package com.lhr.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * @author lhr
 * @additional_information
 */
public class SqlInjectionFilter {
    @Bean
    public GlobalFilter sqlInjectionFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            MultiValueMap<String, String> queryParams = request.getQueryParams();

            String[] sqlKeywords = {
                    "SELECT", "INSERT", "DELETE", "UPDATE", "DROP",
                    "UNION", "' OR '1'='1", "--", ";"
            };

            for (String paramName : queryParams.keySet()) {
                List<String> values = queryParams.get(paramName);
                for (String value : values) {
                    String upperValue = value.toUpperCase();
                    for (String keyword : sqlKeywords) {
                        if (upperValue.contains(keyword)) {
                            throw new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Invalid input detected"
                            );
                        }
                    }
                }
            }

            return chain.filter(exchange);
        };
    }
}
