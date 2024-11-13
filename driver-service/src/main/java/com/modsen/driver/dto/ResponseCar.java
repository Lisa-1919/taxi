package com.modsen.driver.dto;

public record ResponseCar(
        Long id,
        String licensePlate,
        String mark,
        String colour,
        Long driverId,
        Boolean isDeleted
) {
}