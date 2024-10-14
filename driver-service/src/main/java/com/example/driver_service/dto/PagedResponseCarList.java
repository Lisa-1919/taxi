package com.example.driver_service.dto;

import java.util.List;

public record PagedResponseCarList(
        List<ResponseCar> cars,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
}
