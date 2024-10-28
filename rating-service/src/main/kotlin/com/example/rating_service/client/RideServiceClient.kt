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

    @GetMapping("/{id}/exists")
    fun doesRideExist(@PathVariable("id") rideId: Long): ResponseEntity<Boolean>
}