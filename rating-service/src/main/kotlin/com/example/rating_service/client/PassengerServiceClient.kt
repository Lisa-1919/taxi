package com.example.rating_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "passengerService",
    url = "\${feign.client.passenger-service-url}",
    configuration = [RetrieveMessageErrorDecoder::class]
)
interface PassengerServiceClient {
    @GetMapping("/{id}/exists")
    fun doesPassengerExist(@PathVariable("id") passengerId: Long): ResponseEntity<Boolean>?
}