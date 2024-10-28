package com.example.rating_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "ridesService",
    url = "\${feign.client.rides-service-url}",
    configuration = [RetrieveMessageErrorDecoder::class]
)
interface RideServiceClient {

    @GetMapping("/{id}/driver/{driverId}/exists")
    fun doesRideExistForDriver(@PathVariable("id") rideId: Long, @PathVariable("driverId") driverId: Long): ResponseEntity<Boolean>

    @GetMapping("/{id}/passenger/{passengerId}/exists")
    fun doesRideExistForPassenger(@PathVariable("id") rideId: Long, @PathVariable("passengerId") passengerId: Long): ResponseEntity<Boolean>

}