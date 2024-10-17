package com.example.rides_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passengerService", url = "http://localhost:8082/api/v1/passengers")
public interface PassengerServiceClient {

    @GetMapping("/{id}/exists")
    Boolean isPassengerExists(@PathVariable("id") Long passengerId);

}
