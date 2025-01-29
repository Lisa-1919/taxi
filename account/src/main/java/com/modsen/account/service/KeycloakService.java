package com.modsen.account.service;

import com.modsen.account.dto.AuthenticateRequest;
import com.modsen.account.dto.RegistrationRequest;
import com.modsen.account.dto.UserResponse;

import java.util.Map;
import java.util.UUID;

public interface KeycloakService {

    Map<String, Object> login(AuthenticateRequest request);

    UserResponse createUser(RegistrationRequest userRegistrationRecord) throws Exception;

    void deleteUser(UUID userId) throws Exception;

}
