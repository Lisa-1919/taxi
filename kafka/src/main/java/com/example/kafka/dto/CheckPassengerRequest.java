package com.example.kafka.dto;

public record CheckPassengerRequest(
        Long rateId,
        Long passengerId
) {
}
