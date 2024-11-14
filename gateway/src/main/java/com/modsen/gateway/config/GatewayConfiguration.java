package com.modsen.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Value("${route.driver-service.uri}")
    private String driverServiceUri;

    @Value("${route.passenger-service.uri}")
    private String passengerServiceUri;

    @Value("${route.rating-service.uri}")
    private String ratingServiceUri;

    @Value("${route.rides-service.uri}")
    private String ridesServiceUri;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("driver-service", r->r.path("/api/v1/drivers/**", "/api/v1/cars/**")
                        .uri(driverServiceUri))
                .route("passenger-service", r->r.path("/api/v1/passengers/**")
                        .uri(passengerServiceUri))
                .route("rating-service", r->r.path("/api/v1/rates/**")
                        .uri(ratingServiceUri))
                .route("rides-service", r->r.path("/api/v1/rides/**")
                        .uri(ridesServiceUri))
                .build();
    }
}
