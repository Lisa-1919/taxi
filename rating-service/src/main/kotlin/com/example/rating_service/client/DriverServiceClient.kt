package com.example.rating_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "driverService",
    url = "\${feign.client.driver-service-url}",
    configuration = [RetrieveMessageErrorDecoder::class]
)
interface DriverServiceClient {
    @GetMapping("/{id}/exists")
    fun doesDriverExist(@PathVariable("id") driverId: Long): ResponseEntity<Boolean>
}