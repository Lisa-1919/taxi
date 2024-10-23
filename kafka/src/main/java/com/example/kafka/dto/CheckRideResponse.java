package com.example.kafka.dto;

public record CheckRideResponse(
        Long rateId,
        Boolean isExist
) {
}
