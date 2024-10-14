package com.example.driver_service.service;

import com.example.driver_service.dto.PagedResponseDriverList;
import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.DriverMapper;
import com.example.driver_service.repo.DriverRepository;
import com.example.driver_service.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    @Transactional
    public ResponseDriver addDriver(RequestDriver requestDriver) {
        try {
            Driver driver = driverMapper.requestDriverToDriver(requestDriver);
            return driverMapper.driverToResponseDriver(driverRepository.save(driver));
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public ResponseDriver editDriver(Long id, RequestDriver requestDriver) {
        Driver driverFromDB = getOrThrow(id);
        driverMapper.updateDriverFromRequestDriver(requestDriver, driverFromDB);

        try {
            return driverMapper.driverToResponseDriver(driverRepository.save(driverFromDB));
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(ExceptionMessages.DUPLICATE_DRIVER_ERROR.format());
        }
    }

    @Override
    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = getOrThrow(id);
        driverRepository.delete(driver);
    }

    @Override
    public ResponseDriver getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));

        return driverMapper.driverToResponseDriver(driver);
    }

    @Override
    public ResponseDriver getDriverByIdNonDeleted(Long id) {
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
        Page<Driver> driverPage = driverRepository.getAllNonDeleted(pageable);
        return getPagedResponseDriverListFromPage(driverPage);
    }

    private Driver getOrThrow(Long id){
        return driverRepository.getDriverByIdNonDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));
    }

    private PagedResponseDriverList getPagedResponseDriverListFromPage(Page<Driver> driverPage){
        List<ResponseDriver> responseDriverList = driverPage
                .map(driverMapper::driverToResponseDriver).toList();

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
