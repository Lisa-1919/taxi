package com.example.ride.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RequestRide(

        Long driverId,

        @NotNull(message = "Passenger id cannot be null")
        Long passengerId,

        @NotNull(message = "Departure address cannot be null")
        String fromAddress,

        @NotNull(message = "Destination address cannot be null")
        String toAddress,

        @NotNull(message = "Cost cannot be null")
        @DecimalMin(value = "0.01", message = "Minimum ride cost is 0.01")
        BigDecimal cost
) {}
