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
    name = "driver-service",
    url = "\${feign.clients.driver-service.url:}",
    fallback = DriverServiceClientFallback::class,
    configuration = [FeignConfig::class]
)
interface DriverServiceClient {
    @GetMapping("/api/v1/drivers/{id}/exists")
    @CircuitBreaker(name = "driverClient")
    @Retry(name = "driverClientRetry")
    fun doesDriverExist(@PathVariable("id") driverId: UUID): ResponseEntity<Boolean>

}