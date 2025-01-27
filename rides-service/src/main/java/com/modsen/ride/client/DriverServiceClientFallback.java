package com.modsen.ride.client;

import com.modsen.ride.util.ExceptionMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DriverServiceClientFallback implements DriverServiceClient {
    @Override
    public ResponseEntity<Boolean> doesDriverExists(UUID driverId) {
        throw new RuntimeException(ExceptionMessages.UNABLE_TO_REACH_DRIVER_SERVICE.format());
    }
}
