package com.modsen.driver.service;

import com.modsen.driver.dto.CreateDriverRequest;
import com.modsen.driver.dto.PagedResponseDriverList;
import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.dto.ResponseDriver;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DriverService {

    ResponseDriver addDriver(CreateDriverRequest createDriverRequest);

    ResponseDriver editDriver(UUID id, RequestDriver requestDriver);

    void deleteDriver(UUID id);

    ResponseDriver getDriverById(UUID id);

    ResponseDriver getDriverByIdNonDeleted(UUID id);

    PagedResponseDriverList getAllDrivers(Pageable pageable);

    PagedResponseDriverList getAllNonDeletedDrivers(Pageable pageable);

    boolean doesDriverExist(UUID id);
}
