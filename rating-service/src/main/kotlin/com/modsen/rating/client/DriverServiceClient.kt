package com.modsen.rating.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "driver-service",
    fallback = com.modsen.rating.client.DriverServiceClientFallback::class,
    configuration = [com.modsen.rating.client.RetrieveMessageErrorDecoder::class]
)
interface DriverServiceClient {
    @GetMapping("/api/v1/drivers/{id}/exists")
    @CircuitBreaker(name = "driverClient")
    @Retry(name = "driverClientRetry")
    fun doesDriverExist(@PathVariable("id") driverId: Long): ResponseEntity<Boolean>

}