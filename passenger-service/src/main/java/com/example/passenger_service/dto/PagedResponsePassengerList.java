package com.example.passenger_service.dto;

import java.util.List;

public record PagedResponsePassengerList(
        List<ResponsePassenger> passengers,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
}
