package com.example.driver_service.utils;

import com.example.driver_service.dto.*;
import com.example.driver_service.entity.Car;
import com.example.driver_service.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class DriverTestEntityUtils {

    public static final Long DEFAULT_DRIVER_ID = 1L;
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";
    public static final String DEFAULT_EMAIL = "john@example.com";
    public static final String DEFAULT_PHONE_NUMBER = "1234567890";
    public static final String DEFAULT_SEX = "male";
    public static final boolean DEFAULT_IS_DELETED = false;

    public static final String NEW_EMAIL = "new.john@example.com";
    public static final String NEW_PHONE_NUMBER = "1234567000";

    public static final String INVALID_EMAIL = "invalid";
    public static final String INVALID_PHONE_NUMBER = "invalid";

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_TOTAL_ELEMENTS = 1;
    public static final int DEFAULT_TOTAL_PAGES = 1;
    public static final boolean DEFAULT_LAST_PAGE = true;


    public static Driver createTestDriver() {
        return new Driver(
                DEFAULT_DRIVER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_SEX,
                null,
                DEFAULT_IS_DELETED
        );
    }

    public static RequestDriver createTestRequestDriver() {
        return new RequestDriver(
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_SEX
        );
    }

    public static ResponseDriver createTestResponseDriver() {
        return new ResponseDriver(
                DEFAULT_DRIVER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_EMAIL,
                DEFAULT_PHONE_NUMBER,
                DEFAULT_SEX,
                null,
                DEFAULT_IS_DELETED
        );
    }

    public static Driver createUpdatedDriver() {
        return new Driver(
                DEFAULT_DRIVER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                NEW_EMAIL,
                NEW_PHONE_NUMBER,
                DEFAULT_SEX,
                null,
                DEFAULT_IS_DELETED
        );
    }

    public static RequestDriver createUpdateRequestDriver() {
        return new RequestDriver(
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                NEW_EMAIL,
                NEW_PHONE_NUMBER,
                DEFAULT_SEX
        );
    }

    public static ResponseDriver createUpdatedResponseDriver() {
        return new ResponseDriver(
                DEFAULT_DRIVER_ID,
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                NEW_EMAIL,
                NEW_PHONE_NUMBER,
                DEFAULT_SEX,
                null,
                DEFAULT_IS_DELETED
        );
    }

    public static RequestDriver createInvalidRequestDriver() {
        return new RequestDriver(
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                INVALID_EMAIL,
                INVALID_PHONE_NUMBER,
                DEFAULT_SEX
        );
    }

    public static PageRequest createDefaultPageRequest() {
        return PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
    }

    public static Page<Driver> createDefaultDriverPage(List<Driver> drivers) {
        return new PageImpl<>(drivers, createDefaultPageRequest(), DEFAULT_TOTAL_ELEMENTS);
    }

    public static PagedResponseDriverList createDefaultPagedResponseDriverList(List<ResponseDriver> drivers) {
        return new PagedResponseDriverList(
                drivers,
                DEFAULT_TOTAL_ELEMENTS,
                DEFAULT_TOTAL_PAGES,
                DEFAULT_PAGE_NUMBER,
                DEFAULT_PAGE_SIZE,
                DEFAULT_LAST_PAGE
        );
    }

}
