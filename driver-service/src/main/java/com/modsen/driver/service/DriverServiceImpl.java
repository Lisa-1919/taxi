package com.modsen.driver.service;

import com.modsen.driver.dto.CreateDriverRequest;
import com.modsen.driver.dto.PagedResponseDriverList;
import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.dto.ResponseDriver;
import com.modsen.driver.entity.Driver;
import com.modsen.driver.mapper.DriverMapper;
import com.modsen.driver.repo.DriverRepository;
import com.modsen.driver.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private static final String EMAIL = "email";
    private static final String PHONE_NUMBER = "phoneNumber";

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    @Transactional
    @CachePut(cacheNames = "driver", key = "#result.id")
    public ResponseDriver addDriver(CreateDriverRequest createDriverRequest) {
        checkUniqueField(EMAIL, createDriverRequest.email(), driverRepository::existsByEmail);
        checkUniqueField(PHONE_NUMBER, createDriverRequest.phoneNumber(), driverRepository::existsByPhoneNumber);

        Driver driver = driverMapper.createDriverRequestToDriver(createDriverRequest);
        return driverMapper.driverToResponseDriver(driverRepository.save(driver));
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "driver", key = "#id")
    public ResponseDriver editDriver(UUID id, RequestDriver requestDriver) {
        Driver driverFromDB = getOrThrow(id);

        if (!driverFromDB.getEmail().equals(requestDriver.email())) {
            checkUniqueField(EMAIL, requestDriver.email(), driverRepository::existsByEmail);
        }

        if (!driverFromDB.getPhoneNumber().equals(requestDriver.phoneNumber())) {
            checkUniqueField(PHONE_NUMBER, requestDriver.phoneNumber(), driverRepository::existsByPhoneNumber);
        }

        driverMapper.updateDriverFromRequestDriver(requestDriver, driverFromDB);

        return driverMapper.driverToResponseDriver(driverRepository.save(driverFromDB));

    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "driver", key = "#id")
    public void deleteDriver(UUID id) {
        Driver driver = getOrThrow(id);
        driverRepository.delete(driver);
    }

    @Override
    @Cacheable(cacheNames = "driver", key = "#id", unless = "#result == null")
    public ResponseDriver getDriverById(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));

        return driverMapper.driverToResponseDriver(driver);
    }

    @Override
    @Cacheable(cacheNames = "driver", key = "#id", unless = "#result == null")
    public ResponseDriver getDriverByIdNonDeleted(UUID id) {
        Driver driver = getOrThrow(id);
        if (driver.getCar() != null) {
            Hibernate.initialize(driver.getCar());
        }

        return driverMapper.driverToResponseDriver(driver);
    }

    @Override
    public PagedResponseDriverList getAllDrivers(Pageable pageable) {
        Page<Driver> driverPage = driverRepository.findAll(pageable);
        return getPagedResponseDriverListFromPage(driverPage);
    }

    @Override
    public PagedResponseDriverList getAllNonDeletedDrivers(Pageable pageable) {
        Page<Driver> driverPage = driverRepository.findAllNonDeleted(pageable);
        return getPagedResponseDriverListFromPage(driverPage);
    }

    @Override
    @Cacheable(cacheNames = "driverExists", key = "#id")
    public boolean doesDriverExist(UUID id) {
        boolean exists = driverRepository.existsByIdAndIsDeletedFalse(id);
        if (exists) return true;
        else throw new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id));
    }

    private Driver getOrThrow(UUID id) {
        return driverRepository.findDriverByIdNonDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));
    }

    private <T> void checkUniqueField(String fieldName, T fieldValue, Predicate<T> existsFunction) {
        if (existsFunction.test(fieldValue)) {
            throw new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format(fieldName, fieldValue));
        }
    }

    private PagedResponseDriverList getPagedResponseDriverListFromPage(Page<Driver> driverPage) {
        List<ResponseDriver> responseDriverList = driverPage
                .map(driverMapper::driverToResponseDriver)
                .toList();

        return new PagedResponseDriverList(
                responseDriverList,
                driverPage.getNumber(),
                driverPage.getSize(),
                driverPage.getTotalElements(),
                driverPage.getTotalPages(),
                driverPage.isLast()
        );
    }
}

