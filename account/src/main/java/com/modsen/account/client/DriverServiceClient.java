package com.modsen.account.client;

import com.modsen.account.config.FeignConfig;
import com.modsen.account.dto.CreateDriverRequest;
import com.modsen.account.dto.UpdateDriverRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "driver-service",
        configuration = FeignConfig.class,
        fallback = DriverServiceClientFallback.class
)
public interface DriverServiceClient {

    @PostMapping("/api/v1/drivers")
    ResponseEntity<?> createDriver(CreateDriverRequest createDriverRequest);

    @DeleteMapping("/api/v1/drivers/{id}")
    ResponseEntity<?> deleteDriver(@PathVariable UUID id);

    @PutMapping("/api/v1/drivers/{id}")
    ResponseEntity<?> updateDriver(@PathVariable UUID id, @RequestBody UpdateDriverRequest updateDriverRequest);
}
