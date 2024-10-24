package com.example.kafka.dto;

public record CheckDriverResponse(
        Long rateId,
        Boolean isExist
) {
}
