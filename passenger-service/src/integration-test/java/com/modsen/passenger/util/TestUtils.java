package com.modsen.passenger.util;

import java.util.UUID;

public class TestUtils {
    public static final String ACTIVE_PARAM = "active";
    public static final UUID NON_EXISTING_ID = UUID.fromString("11111111-9999-1111-1111-111111111111");
    public static final UUID EXISTING_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID EDIT_ID = UUID.fromString("11111111-0000-1111-1111-111111111111");

    public static final String PASSENGER_BASE_URL = "/api/v1/passengers";
    public static final String PASSENGER_BY_ID_URL = PASSENGER_BASE_URL + "/{id}";
    public static final String PASSENGER_EXISTS_URL = PASSENGER_BY_ID_URL + "/exists";
}
