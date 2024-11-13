package com.modsen.ride.util;

public enum RideStatuses {
    CREATED,
    ACCEPTED(CREATED),
    PICKING_UP(ACCEPTED),
    HEADING_TO_DESTINATION(PICKING_UP),
    COMPLETED(HEADING_TO_DESTINATION),
    CANCELED(CREATED, ACCEPTED, PICKING_UP, HEADING_TO_DESTINATION);

    private final RideStatuses[] previousStatuses;

    RideStatuses(RideStatuses... previousStatuses) {
        this.previousStatuses = previousStatuses;
    }

    public RideStatuses transition(RideStatuses newStatus) throws Exception {
        for (RideStatuses tmp : newStatus.previousStatuses) {
            if (this == tmp) {
                return newStatus;
            }
        }
        throw new Exception(ExceptionMessages.INVALID_STATUS_TRANSITION.format(this, newStatus));
    }

}
