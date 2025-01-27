package com.modsen.account.dto;

import com.modsen.account.util.Roles;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        Roles role
) {
}
