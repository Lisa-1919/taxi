package com.example.rides_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driverService", url = "${feign.client.driver-service-url}")
public interface DriverServiceClient {
    @GetMapping("/{id}/exists")
    ResponseEntity<Void> doesDriverExists(@PathVariable("id") Long driverId);
}
