package com.modsen.account.dto;

import java.util.UUID;

public record CreatePassengerRequest(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
