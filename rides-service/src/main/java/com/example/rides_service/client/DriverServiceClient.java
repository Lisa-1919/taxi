package com.example.rides_service.client;

import com.example.rides_service.config.RetrieveMessageErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driverService", url = "${feign.client.driver-service-url}", configuration = RetrieveMessageErrorDecoder.class)
public interface DriverServiceClient {
    @GetMapping("/{id}/exists")
    ResponseEntity<Boolean> doesDriverExists(@PathVariable("id") Long driverId);
}
