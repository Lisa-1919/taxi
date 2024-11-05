package com.example.driver_service.service;

import com.example.driver_service.dto.PagedResponseDriverList;
import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.DriverMapper;
import com.example.driver_service.repo.DriverRepository;
import com.example.driver_service.util.ExceptionMessages;
import com.example.driver_service.utils.DriverTestEntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DriverServiceImplTest {

    @Autowired
    private DriverServiceImpl driverService;
    @MockBean
    DriverRepository driverRepository;
    @MockBean
    DriverMapper driverMapper;

    private Driver testDriver;
    private RequestDriver testRequestDriver;
    private ResponseDriver testResponseDriver;

    @BeforeEach
    void setUp() {
        testDriver = DriverTestEntityUtils.createTestDriver();
        testRequestDriver = DriverTestEntityUtils.createTestRequestDriver();
        testResponseDriver = DriverTestEntityUtils.createTestResponseDriver();
    }

    @Nested
    class AddDriverTests {
        @Test
        void addDriverOk() {
            mockDriverNonExistence();
            when(driverMapper.requestDriverToDriver(testRequestDriver)).thenReturn(testDriver);
            when(driverRepository.save(testDriver)).thenReturn(testDriver);
            when(driverMapper.driverToResponseDriver(testDriver)).thenReturn(testResponseDriver);

            ResponseDriver responseDriver = driverService.addDriver(testRequestDriver);

            assertEquals(testResponseDriver, responseDriver);
            verify(driverRepository).save(testDriver);
            verify(driverMapper).driverToResponseDriver(testDriver);
        }

        @Test
        void addDriverDuplicateEmail() {
            when(driverRepository.existsByEmail(testRequestDriver.email())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> driverService.addDriver(testRequestDriver));

            verify(driverRepository).existsByEmail(testRequestDriver.email());
            verifyNoMoreInteractions(driverRepository, driverMapper);
        }

        @Test
        void addDriverDuplicatePhoneNumber() {
            when(driverRepository.existsByEmail(testRequestDriver.email())).thenReturn(false);
            when(driverRepository.existsByPhoneNumber(testRequestDriver.phoneNumber())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> driverService.addDriver(testRequestDriver));

            verify(driverRepository).existsByEmail(testRequestDriver.email());
            verify(driverRepository).existsByPhoneNumber(testRequestDriver.phoneNumber());
            verifyNoMoreInteractions(driverMapper, driverRepository);
        }
    }

    @Nested
    class EditDriverTests {
        @Test
        void editDriverOk() {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            RequestDriver requestDriver = DriverTestEntityUtils.createUpdateRequestDriver();
            Driver existingDriver = DriverTestEntityUtils.createTestDriver();
            Driver updatedDriver = DriverTestEntityUtils.createUpdatedDriver();
            ResponseDriver expectedResponseDriver = DriverTestEntityUtils.createUpdatedResponseDriver();

            when(driverRepository.findDriverByIdNonDeleted(driverId)).thenReturn(Optional.of(existingDriver));
            mockDriverNonExistence();
            when(driverRepository.save(existingDriver)).thenReturn(updatedDriver);
            when(driverMapper.driverToResponseDriver(updatedDriver)).thenReturn(expectedResponseDriver);

            ResponseDriver responseDriver = driverService.editDriver(driverId, requestDriver);

            assertEquals(expectedResponseDriver, responseDriver);
            verify(driverMapper).updateDriverFromRequestDriver(eq(requestDriver), eq(existingDriver));
            verify(driverRepository).save(existingDriver);
            verify(driverMapper).driverToResponseDriver(updatedDriver);
        }

        @Test
        void editDriverEntityNotFound() {
            mockDriverNotFound(testDriver.getId());

            assertThrows(EntityNotFoundException.class, () -> driverService.editDriver(testDriver.getId(), testRequestDriver));

            verify(driverRepository).findDriverByIdNonDeleted(testDriver.getId());
            verifyNoMoreInteractions(driverRepository, driverMapper);
        }

        @Test
        void editDriverDuplicateEmail() {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            RequestDriver requestDriver = DriverTestEntityUtils.createUpdateRequestDriver();

            when(driverRepository.findDriverByIdNonDeleted(driverId)).thenReturn(Optional.of(testDriver));
            when(driverRepository.existsByEmail(requestDriver.email())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> driverService.editDriver(driverId, requestDriver));

            verify(driverRepository).findDriverByIdNonDeleted(driverId);
            verify(driverRepository).existsByEmail(requestDriver.email());
            verifyNoMoreInteractions(driverRepository, driverMapper);
        }

        @Test
        void editDriverDuplicatePhoneNumber() {
            Long driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
            RequestDriver requestDriver = DriverTestEntityUtils.createUpdateRequestDriver();

            when(driverRepository.findDriverByIdNonDeleted(driverId)).thenReturn(Optional.of(testDriver));
            when(driverRepository.existsByEmail(requestDriver.email())).thenReturn(false);
            when(driverRepository.existsByPhoneNumber(requestDriver.phoneNumber())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> driverService.editDriver(driverId, requestDriver));

            verify(driverRepository).findDriverByIdNonDeleted(driverId);
            verify(driverRepository).existsByEmail(requestDriver.email());
            verify(driverRepository).existsByPhoneNumber(requestDriver.phoneNumber());
            verifyNoMoreInteractions(driverRepository, driverMapper);
        }
    }

    @Nested
    class DeleteDriverTests {

        @Test
        void deleteDriverOk() {
            when(driverRepository.findDriverByIdNonDeleted(testDriver.getId())).thenReturn(Optional.of(testDriver));

            driverService.deleteDriver(testDriver.getId());

            verify(driverRepository).delete(testDriver);
        }

        @Test
        void deleteDriverEntityNotFound() {
            mockDriverNotFound(testDriver.getId());

            assertThrows(EntityNotFoundException.class, () -> driverService.deleteDriver(testDriver.getId()));

            verify(driverRepository).findDriverByIdNonDeleted(testDriver.getId());
            verifyNoMoreInteractions(driverRepository);
        }
    }

    @Nested
    class GetDriverTests {
        @Test
        void getDriverByIdOk() {
            when(driverRepository.findById(testDriver.getId())).thenReturn(Optional.of(testDriver));
            when(driverMapper.driverToResponseDriver(testDriver)).thenReturn(testResponseDriver);

            ResponseDriver responseDriver = driverService.getDriverById(testDriver.getId());

            assertEquals(testResponseDriver, responseDriver);
        }

        @Test
        void getDriverByIdEntityNotFound() {
            mockDriverNotFound(testDriver.getId());

            assertThrows(EntityNotFoundException.class, () -> driverService.getDriverById(testDriver.getId()));

            verify(driverRepository).findById(testDriver.getId());
            verifyNoMoreInteractions(driverMapper);
        }

        @Test
        void getDriverByIdNonDeletedOk() {
            when(driverRepository.findDriverByIdNonDeleted(testDriver.getId())).thenReturn(Optional.of(testDriver));
            when(driverMapper.driverToResponseDriver(testDriver)).thenReturn(testResponseDriver);

            ResponseDriver responseDriver = driverService.getDriverByIdNonDeleted(testDriver.getId());

            assertEquals(testResponseDriver, responseDriver);
        }

        @Test
        void getDriverByIdNonDeletedEntityNotFound() {
            mockDriverNotFound(testDriver.getId());

            assertThrows(EntityNotFoundException.class, () -> driverService.getDriverByIdNonDeleted(testDriver.getId()));

            verify(driverRepository).findDriverByIdNonDeleted(testDriver.getId());
            verifyNoMoreInteractions(driverMapper);
        }

        @Test
        void getAllDrivers() {
            Pageable pageable = DriverTestEntityUtils.createDefaultPageRequest();
            Page<Driver> driverPage = DriverTestEntityUtils.createDefaultDriverPage(List.of(testDriver));

            when(driverRepository.findAll(pageable)).thenReturn(driverPage);
            when(driverMapper.driverToResponseDriver(testDriver)).thenReturn(testResponseDriver);

            PagedResponseDriverList result = driverService.getAllDrivers(pageable);

            assertPagedResponse(result);

            verify(driverRepository).findAll(pageable);
            verify(driverMapper).driverToResponseDriver(testDriver);
            verifyNoMoreInteractions(driverRepository, driverMapper);
        }

        @Test
        void getAllNonDeletedDrivers() {
            Pageable pageable = DriverTestEntityUtils.createDefaultPageRequest();
            Page<Driver> driverPage = DriverTestEntityUtils.createDefaultDriverPage(List.of(testDriver));

            when(driverRepository.findAllNonDeleted(pageable)).thenReturn(driverPage);
            when(driverMapper.driverToResponseDriver(testDriver)).thenReturn(testResponseDriver);

            PagedResponseDriverList result = driverService.getAllNonDeletedDrivers(pageable);

            assertPagedResponse(result);

            verify(driverRepository).findAllNonDeleted(pageable);
            verify(driverMapper).driverToResponseDriver(testDriver);
            verifyNoMoreInteractions(driverRepository, driverMapper);
        }
    }

    @Nested
    class DriverExistsTests {
        @Test
        void doesDriverExistOk() {
            boolean expected = true;
            when(driverRepository.existsByIdAndIsDeletedFalse(testDriver.getId())).thenReturn(expected);
            boolean actual = driverService.doesDriverExist(testDriver.getId());

            assertEquals(expected, actual);

            verify(driverRepository).existsByIdAndIsDeletedFalse(testDriver.getId());
        }

        @Test
        void doesDriverExistEntityNotFound() {
            mockDriverNotFound(testDriver.getId());
            assertThrows(EntityNotFoundException.class, () -> driverService.doesDriverExist(testDriver.getId()));

            verify(driverRepository).existsByIdAndIsDeletedFalse(testDriver.getId());
        }
    }

    private void mockDriverNonExistence() {
        when(driverRepository.existsByEmail(testRequestDriver.email())).thenReturn(false);
        when(driverRepository.existsByPhoneNumber(testRequestDriver.phoneNumber())).thenReturn(false);
    }

    private void mockDriverNotFound(Long id) {
        when(driverRepository.findDriverByIdNonDeleted(id))
                .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(id)));

    }

    private void assertPagedResponse(PagedResponseDriverList actual) {
        assertEquals(testResponseDriver, actual.drivers().get(0));
        assertEquals(DriverTestEntityUtils.DEFAULT_TOTAL_ELEMENTS, actual.totalElements());
        assertEquals(DriverTestEntityUtils.DEFAULT_TOTAL_PAGES, actual.totalPages());
        assertEquals(DriverTestEntityUtils.DEFAULT_PAGE_NUMBER, actual.pageNumber());
        assertEquals(DriverTestEntityUtils.DEFAULT_PAGE_SIZE, actual.pageSize());
        assertTrue(actual.last());
    }
}