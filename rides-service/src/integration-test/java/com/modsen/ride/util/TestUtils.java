package com.modsen.ride.util;

import java.util.UUID;

public class TestUtils {
    public static final Long EXIST_ID = 10L;
    public static final Long EDIT_ID = 11L;
    public static final Long NON_EXISTING_ID = 999L;
    public static final UUID EDIT_DRIVER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final String PASSENGER_NOT_FOUND_MESSAGE = "Passenger with id %s not found";
    public static final String UPDATE_RIDE_STATUS_MESSAGE = "The status of your ride with id %d changed to %s";

    public static final String RIDE_BASE_URL = "/api/v1/rides";
    public static final String RIDE_BY_ID_URL = RIDE_BASE_URL + "/{id}";
    public static final String RIDE_STATUS_URL = RIDE_BY_ID_URL + "/status";
    public static final String RIDE_EXISTS_FOR_DRIVER_URL = RIDE_BASE_URL + "/{rideId}/driver/{driverId}/exists";
    public static final String RIDE_EXISTS_FOR_PASSENGER_URL = RIDE_BASE_URL + "/{rideId}/passenger/{passengerId}/exists";

    public static final String PASSENGER_BASE_URL = "/api/v1/passengers";
    public static final String PASSENGER_EXISTS_URL = PASSENGER_BASE_URL + "/{id}/exists";

    public static final String DRIVER_BASE_URL = "/api/v1/drivers";
    public static final String DRIVER_EXISTS_URL = DRIVER_BASE_URL + "/{id}/exists";
}
