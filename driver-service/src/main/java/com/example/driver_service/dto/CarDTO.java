package com.example.driver_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CarDTO {
    private Long id;

    @NotNull(message = "License plate cannot be null")
    @Pattern(regexp = "^[A-Z0-9]{1,4}[- ]?[A-Z0-9]{1,4}[- ]?[A-Z0-9]{1,4}$ ", message = "Invalid license plate format")
    private String licensePlate;

    @NotNull(message = "Mark cannot be null")
    private String mark;

    @NotNull(message = "Colour cannot be null")
    private String colour;

    private Long driverId;

    private boolean isDeleted;
}
