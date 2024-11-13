package com.modsen.passenger.dto;

public record ResponsePassenger(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Boolean isDeleted
) {
}
