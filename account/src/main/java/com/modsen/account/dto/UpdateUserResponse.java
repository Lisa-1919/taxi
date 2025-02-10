package com.modsen.account.dto;

import java.util.UUID;

public record UpdateUserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber
) {
}
