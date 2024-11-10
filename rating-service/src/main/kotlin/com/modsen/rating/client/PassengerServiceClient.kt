package com.modsen.rating.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "passenger-service",
    fallback = com.modsen.rating.client.PassengerServiceClientFallback::class,
    configuration = [com.modsen.rating.client.RetrieveMessageErrorDecoder::class]
)
interface PassengerServiceClient {
    @GetMapping("/passengers/api/v1/{id}/exists")
    @CircuitBreaker(name = "passengerClient")
    @Retry(name = "passengerClientRetry")
    fun doesPassengerExist(@PathVariable("id") passengerId: Long): ResponseEntity<Boolean>

}