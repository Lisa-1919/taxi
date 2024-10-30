package com.example.rides_service.client;

import com.example.rides_service.config.RetrieveMessageErrorDecoder;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(
        name = "driverService",
        url = "${feign.client.driver-service-url}",
        fallback = DriverServiceClientFallback.class,
        configuration = RetrieveMessageErrorDecoder.class
)
public interface DriverServiceClient {

    @GetMapping("/{id}/exists")
    @CircuitBreaker(name = "driverClient")
    @Retry(name = "driverClientRetry")
    ResponseEntity<Boolean> doesDriverExists(@PathVariable("id") Long driverId);

}
