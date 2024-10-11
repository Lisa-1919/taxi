package com.example.rides_service.dto;

import java.util.List;

public record ResponseRideList(
        List<ResponseRide> items
) {
}
