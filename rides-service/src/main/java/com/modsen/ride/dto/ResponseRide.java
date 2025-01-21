package com.modsen.ride.dto;

import com.modsen.ride.util.RideStatuses;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ResponseRide(

        Long id,
        UUID driverId,
        UUID passengerId,
        String fromAddress,
        String toAddress,
        RideStatuses rideStatus,
        LocalDateTime orderDateTime,
        BigDecimal cost

) {
}
