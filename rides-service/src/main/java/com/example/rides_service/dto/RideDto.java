package com.example.rides_service.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideDto(

        Long id,

        @NotNull(message = "Driver id cannot be null")
        Long driverId,

        @NotNull(message = "Passenger id cannot be null")
        Long passengerId,

        @NotNull(message = "Departure address cannot be null")
        String fromAddress,

        @NotNull(message = "Destination address cannot be null")
        String toAddress,

        @NotNull(message = "Ride status cannot be null")
        String rideStatus,

        @NotNull(message = "Order date and time cannot be null")
        LocalDateTime orderDateTime,

        @NotNull(message = "Cost cannot be null")
        BigDecimal cost

) {
}
