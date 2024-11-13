package com.example.ride.dto;

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
