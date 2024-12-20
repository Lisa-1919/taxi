package com.modsen.ride.client;

import com.modsen.ride.config.RetrieveMessageErrorDecoder;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "passenger-service",
        fallback = PassengerServiceClientFallback.class,
        configuration = RetrieveMessageErrorDecoder.class
)
public interface PassengerServiceClient {

    @GetMapping("/api/v1/passengers/{id}/exists")
    @CircuitBreaker(name = "passengerClient")
    @Retry(name = "passengerClientRetry")
    ResponseEntity<Boolean> doesPassengerExists(@PathVariable("id") Long passengerId);

}
