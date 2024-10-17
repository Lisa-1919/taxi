package com.example.rides_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passengerService", url = "${feign.client.passenger-service-url}")
public interface PassengerServiceClient {

    @GetMapping("/{id}/exists")
    ResponseEntity<Void> doesPassengerExists(@PathVariable("id") Long passengerId);

}
