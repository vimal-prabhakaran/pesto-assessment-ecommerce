package com.pesto.apigatewayservice.config;

import com.pesto.apigatewayservice.validator.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Configuration
@EnableHystrix
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-manager", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.filter(authenticationFilter).requestRateLimiter(l -> l.setRateLimiter(redisRateLimiter())))
                        .uri("lb://auth-manager"))
                .route("product-manager-service", r -> r.path("/api/v1/products/search", "/api/v1/seller/products",
                                "/api/v1/seller/product")
                        .filters(f -> f.filter(authenticationFilter).requestRateLimiter(l -> l.setRateLimiter(redisRateLimiter())))
                        .uri("lb://product-manager-service"))
                .route("order-manager-service", r -> r.path("/api/v1/order", "/api/v1/order/**","/api/v1/orders"
                                ,"/api/v1/seller/orders","/api/v1/seller/order/**")
                        .filters(f -> f.filter(authenticationFilter).requestRateLimiter(l -> l.setRateLimiter(redisRateLimiter())))
                        .uri("lb://order-manager-service"))

                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(50, 51);
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
    }

}
