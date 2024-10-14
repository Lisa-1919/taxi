package com.example.rides_service.dto;

import java.util.List;

public record PagedResponseRideList(
        List<ResponseRide> rides,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
}
