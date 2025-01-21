package com.modsen.account.client;

import com.modsen.account.config.FeignConfig;
import com.modsen.account.dto.CreatePassengerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
}
