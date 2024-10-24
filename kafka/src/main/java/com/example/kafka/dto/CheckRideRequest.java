package com.example.kafka.dto;

import com.example.kafka.util.UserType;

public record CheckRideRequest(
        Long rateId,
        Long rideId,
        Long userId,
        UserType userType
) {
}
