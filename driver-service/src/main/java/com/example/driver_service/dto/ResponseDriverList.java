package com.example.driver_service.dto;

import java.util.List;

public record ResponseDriverList(
        List<ResponseDriver> items
) {
}
