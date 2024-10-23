package com.example.kafka.dto;

public record CheckDriverRequest(
        Long rateId,
        Long driverId
) {
}
