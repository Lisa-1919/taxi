package com.example.rides_service.dto;

import com.example.rides_service.util.RideStatuses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResponseRide(

        Long id,
        Long driverId,
        Long passengerId,
        String fromAddress,
        String toAddress,
        RideStatuses rideStatus,
        LocalDateTime orderDateTime,
        BigDecimal cost

) {
}
