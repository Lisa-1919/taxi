package com.modsen.account.mapper;

import com.modsen.account.dto.CreateDriverRequest;
import com.modsen.account.dto.CreatePassengerRequest;
import com.modsen.account.dto.RegistrationRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RequestMapper {

    public CreateDriverRequest registrationRequestToCreateDriverRequest(RegistrationRequest registrationRequest, UUID userId) {
        return new CreateDriverRequest(
                userId,
                registrationRequest.firstName(),
                registrationRequest.lastName(),
                registrationRequest.email(),
                registrationRequest.phoneNumber(),
                registrationRequest.sex()
        );
    }

    public CreatePassengerRequest registrationRequestToCreatePassengerRequest(RegistrationRequest registrationRequest, UUID userId) {
        return new CreatePassengerRequest(
                userId,
                registrationRequest.firstName(),
                registrationRequest.lastName(),
                registrationRequest.email(),
                registrationRequest.phoneNumber()
        );
    }

}
