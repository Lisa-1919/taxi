package com.example.kafka.dto;

public record CheckPassengerResponse(
        Long rateId,
        Boolean isExist
) {
}
