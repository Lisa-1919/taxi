package com.modsen.driver.util;

public class TestUtils {
    public static final String ACTIVE_PARAM = "active";
    public static final Long NON_EXISTING_ID = 999L;
    public static final Long EXISTING_ID = 10L;
    public static final Long EDIT_ID = 11L;

    public static final String DRIVER_BASE_URL = "/api/v1/drivers";
    public static final String DRIVER_BY_ID_URL = DRIVER_BASE_URL + "/{id}";
    public static final String DRIVER_EXISTS_URL = DRIVER_BY_ID_URL + "/exists";

    public static final String CAR_BASE_URL = "/api/v1/cars";
    public static final String CAR_BY_ID_URL = CAR_BASE_URL + "/{id}";
}
