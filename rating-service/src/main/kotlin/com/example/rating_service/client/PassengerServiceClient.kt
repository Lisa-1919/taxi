package com.example.rating_service.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "passengerService",
    url = "\${feign.client.passenger-service-url}",
    fallback = PassengerServiceClientFallback::class,
    configuration = [RetrieveMessageErrorDecoder::class]
)
interface PassengerServiceClient {
    @GetMapping("/{id}/exists")
    @CircuitBreaker(name = "passengerClient")
    @Retry(name = "passengerClientRetry")
    fun doesPassengerExist(@PathVariable("id") passengerId: Long): ResponseEntity<Boolean>

}