package com.modsen.passenger.util;

public class TestUtils {
    public static final String ACTIVE_PARAM = "active";
    public static final Long NON_EXISTING_ID = 999L;
    public static final Long EXISTING_ID = 10L;
    public static final Long EDIT_ID = 11L;

    public static final String PASSENGER_BASE_URL = "/api/v1/passengers";
    public static final String PASSENGER_BY_ID_URL = PASSENGER_BASE_URL + "/{id}";
    public static final String PASSENGER_EXISTS_URL = PASSENGER_BY_ID_URL + "/exists";
}
