package com.modsen.driver.unit.service;

import com.modsen.driver.dto.CreateDriverRequest;
import com.modsen.driver.dto.PagedResponseDriverList;
import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.dto.ResponseDriver;
import com.modsen.driver.entity.Driver;
import com.modsen.driver.mapper.DriverMapper;
import com.modsen.driver.repo.DriverRepository;
import com.modsen.driver.service.DriverServiceImpl;
import com.modsen.driver.util.ExceptionMessages;
import com.modsen.driver.utils.DriverTestEntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DriverServiceImplTest {

    @InjectMocks
    private DriverServiceImpl driverService;
    @Mock
    DriverRepository driverRepository;
    @Mock
    DriverMapper driverMapper;

    private Driver testDriver;
    private RequestDriver testRequestDriver;
    private ResponseDriver testResponseDriver;
    private UUID driverId;
    private CreateDriverRequest createDriverRequest;

    @BeforeEach
    void setUp() {
        driverId = DriverTestEntityUtils.DEFAULT_DRIVER_ID;
        testDriver = DriverTestEntityUtils.createTestDriver();
        testRequestDriver = DriverTestEntityUtils.createTestRequestDriver();
        testResponseDriver = DriverTestEntityUtils.createTestResponseDriver();
        createDriverRequest = DriverTestEntityUtils.createDriverRequest();
    }

    @Nested
    class AddDriverTests {
        @Test
        void addDriverOk() {
            mockDriverNonExistence();
            when(driverMapper.createDriverRequestToDriver(createDriverRequest)).thenReturn(testDriver);
            when(driverRepository.save(testDriver)).thenReturn(testDriver);
            when(driverMapper.driverToResponseDriver(testDriver)).thenReturn(testResponseDriver);

            ResponseDriver responseDriver = driverService.addDriver(createDriverRequest);

            assertEquals(testResponseDriver, responseDriver);
            verify(driverRepository).save(testDriver);
            verify(driverMapper).driverToResponseDriver(testDriver);
        }

        @Test
        void addDriverDuplicateEmail() {
            when(driverRepository.existsByEmail(createDriverRequest.email())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> driverService.addDriver(createDriverRequest));

            verify(driverRepository).existsByEmail(createDriverRequest.email());
            verifyNoMoreInteractions(driverRepository, driverMapper);
        }

        @Test
        void addDriverDuplicatePhoneNumber() {
            when(driverRepository.existsByEmail(createDriverRequest.email())).thenReturn(false);
            when(driverRepository.existsByPhoneNumber(createDriverRequest.phoneNumber())).thenReturn(true);

            assertThrows(DataIntegrityViolationException.class, () -> driverService.addDriver(createDriverRequest));

            verify(driverRepository).existsByEmail(createDriverRequest.email());
            verify(driverRepository).existsByPhoneNumber(createDriverRequest.phoneNumber());
            verifyNoMoreInteractions(driverMapper, driverRepository);
        }
    }

    @Nested
    class EditDriverTests {
        @Test
        void editDriverOk() {
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
            mockDriverNotFound(driverId);

            assertThrows(EntityNotFoundException.class, () -> driverService.editDriver(driverId, testRequestDriver));

            verify(driverRepository).findDriverByIdNonDeleted(driverId);
            verifyNoMoreInteractions(driverRepository, driverMapper);
        }

        @Test
        void editDriverDuplicateEmail() {
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
            when(driverRepository.findDriverByIdNonDeleted(driverId)).thenReturn(Optional.of(testDriver));

            driverService.deleteDriver(driverId);

            verify(driverRepository).delete(testDriver);
        }

        @Test
        void deleteDriverEntityNotFound() {
            mockDriverNotFound(driverId);

            assertThrows(EntityNotFoundException.class, () -> driverService.deleteDriver(driverId));

            verify(driverRepository).findDriverByIdNonDeleted(driverId);
            verifyNoMoreInteractions(driverRepository);
        }
    }

    @Nested
    class GetDriverTests {
        @Test
        void getDriverByIdOk() {
            when(driverRepository.findById(driverId)).thenReturn(Optional.of(testDriver));
            when(driverMapper.driverToResponseDriver(testDriver)).thenReturn(testResponseDriver);

            ResponseDriver responseDriver = driverService.getDriverById(driverId);

            assertEquals(testResponseDriver, responseDriver);
        }

        @Test
        void getDriverByIdEntityNotFound() {
            mockDriverNotFound(driverId);

            assertThrows(EntityNotFoundException.class, () -> driverService.getDriverById(driverId));

            verify(driverRepository).findById(driverId);
            verifyNoMoreInteractions(driverMapper);
        }

        @Test
        void getDriverByIdNonDeletedOk() {
            when(driverRepository.findDriverByIdNonDeleted(driverId)).thenReturn(Optional.of(testDriver));
            when(driverMapper.driverToResponseDriver(testDriver)).thenReturn(testResponseDriver);

            ResponseDriver responseDriver = driverService.getDriverByIdNonDeleted(driverId);

            assertEquals(testResponseDriver, responseDriver);
        }

        @Test
        void getDriverByIdNonDeletedEntityNotFound() {
            mockDriverNotFound(driverId);

            assertThrows(EntityNotFoundException.class, () -> driverService.getDriverByIdNonDeleted(driverId));

            verify(driverRepository).findDriverByIdNonDeleted(driverId);
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
            when(driverRepository.existsByIdAndIsDeletedFalse(driverId)).thenReturn(expected);
            boolean actual = driverService.doesDriverExist(driverId);

            assertEquals(expected, actual);

            verify(driverRepository).existsByIdAndIsDeletedFalse(driverId);
        }

        @Test
        void doesDriverExistEntityNotFound() {
            mockDriverNotFound(driverId);
            assertThrows(EntityNotFoundException.class, () -> driverService.doesDriverExist(driverId));

            verify(driverRepository).existsByIdAndIsDeletedFalse(driverId);
        }
    }

    private void mockDriverNonExistence() {
        when(driverRepository.existsByEmail(testRequestDriver.email())).thenReturn(false);
        when(driverRepository.existsByPhoneNumber(testRequestDriver.phoneNumber())).thenReturn(false);
    }

    private void mockDriverNotFound(UUID id) {
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