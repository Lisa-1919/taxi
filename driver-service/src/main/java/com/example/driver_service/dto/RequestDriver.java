package com.example.driver_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RequestDriver(
        @NotNull(message = "First name cannot be null")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotNull(message = "Last name cannot be null")
        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        String lastName,

        @Email(message = "Invalid email format")
        @NotNull(message = "Email cannot be null")
        String email,

        @NotNull(message = "Phone number cannot be null")
        @Pattern(regexp = PHONE_NUMBER_REGEX, message = "Invalid phone number format")
        String phoneNumber,

        String sex

) {
    private static final String PHONE_NUMBER_REGEX = "^(\\+)?((\\d{2,3}) ?\\d|\\d)(([ -]?\\d)|( ?(\\d{2,3}) ?)){5,12}\\d$";
}