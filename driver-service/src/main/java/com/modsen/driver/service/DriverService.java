package com.modsen.driver.service;

import com.modsen.driver.dto.PagedResponseDriverList;
import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.dto.ResponseDriver;
import org.springframework.data.domain.Pageable;

public interface DriverService {

    ResponseDriver addDriver(RequestDriver requestDriver);

    ResponseDriver editDriver(Long id, RequestDriver requestDriver);

    void deleteDriver(Long id);

    ResponseDriver getDriverById(Long id);

    ResponseDriver getDriverByIdNonDeleted(Long id);

    PagedResponseDriverList getAllDrivers(Pageable pageable);

    PagedResponseDriverList getAllNonDeletedDrivers(Pageable pageable);

    boolean doesDriverExist(Long id);
}
