package com.modsen.ride.client;

import com.modsen.ride.util.ExceptionMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class PassengerServiceClientFallback implements PassengerServiceClient {
    @Override
    public ResponseEntity<Boolean> doesPassengerExists(UUID passengerId) {
        throw new RuntimeException(ExceptionMessages.UNABLE_TO_REACH_PASSENGER_SERVICE.format());
    }
}
