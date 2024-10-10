package com.example.passenger_service.dto;

import java.util.List;

public record ResponsePassengerList(
        List<ResponsePassenger> items
) {
}
