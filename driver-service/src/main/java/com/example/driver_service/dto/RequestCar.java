package com.example.driver_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RequestCar(
        @NotNull(message = "License plate cannot be null")
        @Pattern(regexp = LICENSE_PLATE_REGEX, message = "Invalid license plate format")
        String licensePlate,

        @NotNull(message = "Mark cannot be null")
        String mark,

        @NotNull(message = "Colour cannot be null")
        String colour,

        @NotNull(message = "Driver id cannot be null")
        Long driverId
) {
    private static final String LICENSE_PLATE_REGEX = "^[A-Z0-9]{1,4}[- ]?[A-Z0-9]{1,4}[- ]?[A-Z0-9]{1,4}$";
}