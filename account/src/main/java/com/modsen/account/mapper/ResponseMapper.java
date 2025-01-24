package com.modsen.account.mapper;

import com.modsen.account.dto.RegistrationRequest;
import com.modsen.account.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResponseMapper {

    public UserResponse toUserResponse(UUID userId, RegistrationRequest registrationRequest){
        return new UserResponse(
                userId,
                registrationRequest.email(),
                registrationRequest.firstName(),
                registrationRequest.lastName(),
                registrationRequest.phoneNumber(),
                registrationRequest.role()
        );
    }

}
