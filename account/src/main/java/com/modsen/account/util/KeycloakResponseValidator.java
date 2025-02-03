package com.modsen.account.util;

import com.modsen.exception_handler.exception.CreateUserException;
import com.modsen.exception_handler.exception.UserAlreadyExistsException;
import jakarta.ws.rs.core.Response;
import org.springframework.http.HttpStatus;

public class KeycloakResponseValidator {

    public static void validateCreateUserResponse(Response response) {
        int status = response.getStatus();

        if (status == HttpStatus.CONFLICT.value()) {
            throw new UserAlreadyExistsException("User already exists");
        } else if (status == HttpStatus.BAD_REQUEST.value()) {
            throw new IllegalArgumentException("Invalid user data provided");
        } else if (status != HttpStatus.CREATED.value()) {
            throw new CreateUserException("Failed to create user due to unknown error");
        }
    }

}
