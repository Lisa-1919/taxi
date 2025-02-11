package com.modsen.account.client;

import com.modsen.account.dto.CreatePassengerRequest;
import com.modsen.account.dto.UpdatePassengerRequest;
import com.modsen.account.util.ExceptionMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PassengerServiceClientFallback implements PassengerServiceClient {

    @Override
    public ResponseEntity<?> createPassenger(CreatePassengerRequest createPassengerRequest) {
        throw new RuntimeException(ExceptionMessages.UNABLE_TO_REACH_PASSENGER_SERVICE.format());
    }

    @Override
    public ResponseEntity<?> deletePassenger(UUID id) {
        throw new RuntimeException(ExceptionMessages.UNABLE_TO_REACH_PASSENGER_SERVICE.format());
    }

    @Override
    public ResponseEntity<?> updatePassenger(UUID id, UpdatePassengerRequest updatePassengerRequest) {
        throw new RuntimeException(ExceptionMessages.UNABLE_TO_REACH_PASSENGER_SERVICE.format());
    }

}
