package com.modsen.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("driver-service", r->r.path("/api/v1/drivers/**")
                        .uri("http://localhost:8081"))
                .route("passenger-service", r->r.path("/api/v1/passengers/**")
                        .uri("http://localhost:8082"))
                .route("rating-service", r->r.path("/api/v1/rates/**")
                        .uri("http://localhost:8084"))
                .route("rides-service", r->r.path("/api/v1/rides/**")
                        .uri("http://localhost:8083"))
                .build();
    }
}
