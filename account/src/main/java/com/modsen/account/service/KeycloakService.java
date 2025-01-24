package com.modsen.account.service;

import com.modsen.account.dto.RegistrationRequest;
import com.modsen.account.dto.UserResponse;

import java.util.UUID;

public interface KeycloakService {

    UserResponse createUser(RegistrationRequest userRegistrationRecord) throws Exception;

    void deleteUser(UUID userId) throws Exception;

}
