package com.example.rides_service.dto;

import com.example.rides_service.util.RideStatuses;

public record RequestChangeStatus(
        RideStatuses newStatus
) {
}
