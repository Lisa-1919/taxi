package com.modsen.account.dto;

public record UpdatePassengerRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {}