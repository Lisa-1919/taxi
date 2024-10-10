package com.example.driver_service.dto;

public record ResponseCar(
        Long id,
        String licensePlate,
        String mark,
        String colour,
        Long driverId,
        Boolean isDeleted
) {
}