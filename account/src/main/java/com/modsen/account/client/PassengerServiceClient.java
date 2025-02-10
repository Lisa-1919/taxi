package com.modsen.account.client;

import com.modsen.account.config.FeignConfig;
import com.modsen.account.dto.CreatePassengerRequest;
import com.modsen.account.dto.UpdatePassengerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "passenger-service",
        configuration = FeignConfig.class,
        fallback = PassengerServiceClientFallback.class
)
public interface PassengerServiceClient {

    @PostMapping("/api/v1/passengers")
    ResponseEntity<?> createPassenger(CreatePassengerRequest createPassengerRequest);

    @DeleteMapping("/api/v1/passengers/{id}")
    ResponseEntity<?> deletePassenger(@PathVariable UUID id);

    @PutMapping("/api/v1/passengers/{id}")
    ResponseEntity<?> updatePassenger(@PathVariable UUID id, @RequestBody UpdatePassengerRequest updatePassengerRequest);
}
