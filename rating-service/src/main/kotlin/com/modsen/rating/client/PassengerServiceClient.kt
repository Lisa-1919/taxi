package com.modsen.rating.client

import com.modsen.rating.config.FeignConfig
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import java.util.UUID
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "passenger-service",
    url = "\${feign.clients.passenger-service.url:}",
    fallback = PassengerServiceClientFallback::class,
    configuration = [FeignConfig::class]
)
interface PassengerServiceClient {
    @GetMapping("/api/v1/passengers/{id}/exists")
    @CircuitBreaker(name = "passengerClient")
    @Retry(name = "passengerClientRetry")
    fun doesPassengerExist(@PathVariable("id") passengerId: UUID): ResponseEntity<Boolean>

}