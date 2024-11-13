package com.modsen.driver.dto;

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
    private static final String LICENSE_PLATE_REGEX = "^\\b([A-Z]{1} \\d{4} [A-Z]{2}-\\d|\\d{4} [A-Z]-\\d|[A-Z]{2} \\d{4}-\\d)\\b$";
}