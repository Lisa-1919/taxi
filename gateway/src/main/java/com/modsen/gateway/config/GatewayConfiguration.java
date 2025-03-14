package com.modsen.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path( "/driver-service/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://driver-service"))
                .route(r -> r.path("/passenger-service/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://passenger-service"))
                .route(r -> r.path("/rating-service/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://rating-service"))
                .route(r -> r.path("/rides-service/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://rides-service"))
                .route(r -> r.path("/account-service/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://account-service"))
                .route(r -> r.path("/storage-service/v3/api-docs").and().method(HttpMethod.GET)
                        .uri("lb://storage-service"))
                .build();
    }

}