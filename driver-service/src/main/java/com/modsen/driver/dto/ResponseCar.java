package com.modsen.driver.dto;

import java.util.UUID;

public record ResponseCar(
        Long id,
        String licensePlate,
        String mark,
        String colour,
        UUID driverId,
        Boolean isDeleted
) {
}