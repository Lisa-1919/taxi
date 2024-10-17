package com.example.driver_service.dto;

import java.util.List;

public record PagedResponseDriverList(
        List<ResponseDriver> drivers,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {
}