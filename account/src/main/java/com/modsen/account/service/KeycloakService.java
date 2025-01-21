package com.modsen.account.service;

import com.modsen.account.dto.RegistrationRequest;

import java.util.UUID;

public interface KeycloakService {

    void createUser(RegistrationRequest userRegistrationRecord) throws Exception;

    void deleteUser(UUID userId) throws Exception;

}
