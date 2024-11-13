package com.example.ride.dto;

import com.example.ride.util.RideStatuses;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
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
