package com.example.rating_service.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "driverService",
    url = "\${feign.client.driver-service-url}",
    fallback = DriverServiceClientFallback::class,
    configuration = [RetrieveMessageErrorDecoder::class]
)
interface DriverServiceClient {
    @GetMapping("/{id}/exists")
    @CircuitBreaker(name = "driverClient")
    @Retry(name = "driverClientRetry")
    fun doesDriverExist(@PathVariable("id") driverId: Long): ResponseEntity<Boolean>

}