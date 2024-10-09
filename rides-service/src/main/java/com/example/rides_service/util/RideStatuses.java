package com.example.rides_service.util;

public enum RideStatuses {
    CREATED("created"),
    ACCEPTED("accepted"),
    PICKING_UP("picking up"),
    HEADING_TO_DESTINATION("heading to destination"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String status;

    RideStatuses(String status) {
        this.status = status;
    }
}
