package com.modsen.rating.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "rides-service",
    fallback = RideServiceClientFallback::class,
    configuration = [com.modsen.rating.client.RetrieveMessageErrorDecoder::class]
)
interface RideServiceClient {

    @GetMapping("/api/v1/rides/{id}/driver/{driverId}/exists")
    @CircuitBreaker(name = "rideClient")
    @Retry(name = "rideClientRetry")
    fun doesRideExistForDriver(@PathVariable("id") rideId: Long, @PathVariable("driverId") driverId: Long): ResponseEntity<Boolean>

    @GetMapping("/api/v1/rides/{id}/passenger/{passengerId}/exists")
    @CircuitBreaker(name = "rideClient")
    @Retry(name = "rideClientRetry")
    fun doesRideExistForPassenger(@PathVariable("id") rideId: Long, @PathVariable("passengerId") passengerId: Long): ResponseEntity<Boolean>

}