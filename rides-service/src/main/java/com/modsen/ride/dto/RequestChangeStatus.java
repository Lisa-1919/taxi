package com.modsen.ride.dto;

import com.modsen.ride.util.RideStatuses;

public record RequestChangeStatus(
        RideStatuses newStatus
) {
}
