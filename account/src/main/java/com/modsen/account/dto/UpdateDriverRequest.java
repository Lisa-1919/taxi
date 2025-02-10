package com.modsen.account.dto;

public record UpdateDriverRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String sex
) {}