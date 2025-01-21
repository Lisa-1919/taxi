package com.modsen.rating.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import java.util.UUID
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "passenger-service",
    fallback = PassengerServiceClientFallback::class,
    configuration = [RetrieveMessageErrorDecoder::class]
)
interface PassengerServiceClient {
    @GetMapping("/passengers/api/v1/{id}/exists")
    @CircuitBreaker(name = "passengerClient")
    @Retry(name = "passengerClientRetry")
    fun doesPassengerExist(@PathVariable("id") passengerId: UUID): ResponseEntity<Boolean>

}