package com.example.rides_service.client;

import com.example.rides_service.config.RetrieveMessageErrorDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passengerService", url = "${feign.client.passenger-service-url}", configuration = RetrieveMessageErrorDecoder.class)
public interface PassengerServiceClient {

    @GetMapping("/{id}/exists")
    ResponseEntity<Boolean> doesPassengerExists(@PathVariable("id") Long passengerId);

}
