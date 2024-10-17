package com.example.rides_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driverService", url = "http://localhost:8081/api/v1/drivers")
public interface DriverServiceClient {
    @GetMapping("/{id}/exists")
    Boolean isDriverExists(@PathVariable("id") Long driverId);
}
