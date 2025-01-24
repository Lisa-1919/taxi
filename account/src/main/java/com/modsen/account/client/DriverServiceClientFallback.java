package com.modsen.account.client;

import com.modsen.account.dto.CreateDriverRequest;
import com.modsen.account.util.ExceptionMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DriverServiceClientFallback implements DriverServiceClient {

    @Override
    public ResponseEntity<?> createDriver(CreateDriverRequest createDriverRequest) {
        throw new RuntimeException(ExceptionMessages.UNABLE_TO_REACH_DRIVER_SERVICE.format());
    }

    @Override
    public ResponseEntity<?> deleteDriver(UUID id) {
        throw new RuntimeException(ExceptionMessages.UNABLE_TO_REACH_DRIVER_SERVICE.format());
    }

}
