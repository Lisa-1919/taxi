package com.example.rides_service.dto;

import com.example.rides_service.util.RideStatuses;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideDto(

        Long id,

        Long driverId,

        @NotNull(message = "Passenger id cannot be null")
        Long passengerId,

        @NotNull(message = "Departure address cannot be null")
        String fromAddress,

        @NotNull(message = "Destination address cannot be null")
        String toAddress,

        RideStatuses rideStatus,

        LocalDateTime orderDateTime,

        @NotNull(message = "Cost cannot be null")
        @DecimalMin(value = "0.01", message = "Minimum ride cost is 0.01")
        BigDecimal cost

) {
}
