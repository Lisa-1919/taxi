package com.example.passenger_service.dto;

public record ResponsePassenger(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        Boolean isDeleted
) {
}
