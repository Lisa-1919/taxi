package com.example.rides_service.client;

import com.example.rides_service.util.ExceptionMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PassengerServiceClientFallback implements PassengerServiceClient {
    @Override
    public ResponseEntity<Boolean> doesPassengerExists(Long passengerId) {
        throw new RuntimeException(ExceptionMessages.UNABLE_TO_REACH_PASSENGER_SERVICE.format());
    }
}
