package com.modsen.passenger.dto;

import java.util.UUID;

public record ResponsePassenger(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Boolean isDeleted
) {
}
