package com.modsen.driver.utils;

import com.modsen.driver.dto.PagedResponseCarList;
import com.modsen.driver.dto.RequestCar;
import com.modsen.driver.dto.ResponseCar;
import com.modsen.driver.entity.Car;
import com.modsen.driver.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class CarTestEntityUtils {
    public static final Long DEFAULT_CAR_ID = 1L;
    public static final String DEFAULT_LICENSE_PLATE = "AB 1234-7";
    public static final String INVALID_LICENSE_PLATE = "INVALID!";
    public static final String DEFAULT_MARK = "mark";
    public static final String DEFAULT_COLOUR = "colour";
    public static final Long DEFAULT_DRIVER_ID = 1L;
    public static final boolean DEFAULT_DELETED_STATUS = false;

    public static final String UPDATED_LICENSE_PLATE = "newLicensePlate";
    public static final String UPDATED_MARK = "newMark";
    public static final String UPDATED_COLOUR = "newColour";

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_TOTAL_ELEMENTS = 1;
    public static final int DEFAULT_TOTAL_PAGES = 1;
    public static final boolean DEFAULT_LAST_PAGE = true;

    public static Driver createTestDriver() {
        Driver driver = new Driver();
        driver.setId(DEFAULT_DRIVER_ID);
        return driver;
    }

    public static Car createTestCar() {
        return new Car(
                DEFAULT_CAR_ID,
                DEFAULT_LICENSE_PLATE,
                DEFAULT_MARK,
                DEFAULT_COLOUR,
                createTestDriver(),
                DEFAULT_DELETED_STATUS
        );
    }

    public static RequestCar createTestRequestCar() {
        return new RequestCar(
                DEFAULT_LICENSE_PLATE,
                DEFAULT_MARK,
                DEFAULT_COLOUR,
                DEFAULT_DRIVER_ID
        );
    }

    public static RequestCar createTestRequestCar(Long driverId) {
        return new RequestCar(
                DEFAULT_LICENSE_PLATE,
                DEFAULT_MARK,
                DEFAULT_COLOUR,
                driverId
        );
    }

    public static ResponseCar createTestResponseCar() {
        return new ResponseCar(
                DEFAULT_CAR_ID,
                DEFAULT_LICENSE_PLATE,
                DEFAULT_MARK,
                DEFAULT_COLOUR,
                DEFAULT_DRIVER_ID,
                DEFAULT_DELETED_STATUS
        );
    }

    public static RequestCar createInvalidRequestCar() {
        return new RequestCar(
                INVALID_LICENSE_PLATE,
                null,
                DEFAULT_COLOUR,
                null
        );
    }

    public static Car createUpdatedCar() {
        return new Car(
                DEFAULT_CAR_ID,
                UPDATED_LICENSE_PLATE,
                UPDATED_MARK,
                UPDATED_COLOUR,
                createTestDriver(),
                DEFAULT_DELETED_STATUS
        );
    }

    public static ResponseCar createUpdatedResponseCar() {
        return new ResponseCar(
                DEFAULT_CAR_ID,
                UPDATED_LICENSE_PLATE,
                UPDATED_MARK,
                UPDATED_COLOUR,
                DEFAULT_DRIVER_ID,
                DEFAULT_DELETED_STATUS
        );
    }

    public static PageRequest createDefaultPageRequest() {
        return PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
    }

    public static Page<Car> createDefaultCarPage(List<Car> cars) {
        return new PageImpl<>(cars, createDefaultPageRequest(), DEFAULT_TOTAL_ELEMENTS);
    }

    public static PagedResponseCarList createDefaultPagedResponseCarList(List<ResponseCar> cars) {
        return new PagedResponseCarList(
                cars,
                DEFAULT_PAGE_NUMBER,
                DEFAULT_PAGE_SIZE,
                DEFAULT_TOTAL_ELEMENTS,
                DEFAULT_TOTAL_PAGES,
                DEFAULT_LAST_PAGE
        );
    }
}
