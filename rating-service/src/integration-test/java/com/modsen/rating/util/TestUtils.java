package com.modsen.rating.util;

public class TestUtils {
    public static final Long EXIST_Id = 10L;
    public static final Long NON_EXISTING_ID = 999L;
    public static final String RIDE_NOT_FOUND_MESSAGE = "Ride with id %d not found";
    public static final Integer TOTAL_ELEMENTS = 2;

    public static final String RATE_BASE_URL = "/api/v1/rates";
    public static final String RATE_BY_ID_URL = RATE_BASE_URL + "/{id}";

    public static final String PASSENGER_BASE_URL = "/api/v1/passengers";
    public static final String PASSENGER_EXISTS_URL = PASSENGER_BASE_URL + "/{id}/exists";

    public static final String DRIVER_BASE_URL = "/api/v1/drivers";
    public static final String DRIVER_EXISTS_URL = DRIVER_BASE_URL + "/{id}/exists";

    public static final String RIDE_EXISTS_FOR_PASSENGER_URL = "/api/v1/rides/{rideId}/passenger/{passengerId}/exists";
}
