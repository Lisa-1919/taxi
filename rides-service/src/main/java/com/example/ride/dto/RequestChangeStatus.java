package com.example.ride.dto;

import com.example.ride.util.RideStatuses;

public record RequestChangeStatus(
        RideStatuses newStatus
) {
}
