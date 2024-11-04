package com.example.driver_service.service;

import com.example.driver_service.dto.PagedResponseCarList;
import com.example.driver_service.dto.RequestCar;
import com.example.driver_service.dto.ResponseCar;
import com.example.driver_service.entity.Car;
import com.example.driver_service.entity.Driver;
import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.repo.CarRepository;
import com.example.driver_service.repo.DriverRepository;
import com.example.driver_service.util.ExceptionMessages;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CarServiceImplTest {

    @Autowired
    private CarServiceImpl carService;
    @MockBean
    CarRepository carRepository;
    @MockBean
    DriverRepository driverRepository;
    @MockBean
    CarMapper carMapper;
    private Driver testDriver;
    private Car testCar;
    private ResponseCar testResponseCar;
    private RequestCar testRequestCar;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1L);
        testCar = new Car(1L, "licensePlate", "mark", "colour", testDriver, false);
        testResponseCar = new ResponseCar(1L, "licensePlate", "mark", "colour", 1L, false);
        testRequestCar = new RequestCar("licensePlate", "mark", "colour", 1L);
    }

    @Nested
    class AddCarTests {
        @Test
        void addCarOk() {
            mockCarRepositoryLicensePlateExists(testRequestCar.licensePlate(), false);
            when(carMapper.requestCarToCar(testRequestCar)).thenReturn(testCar);
            when(driverRepository.findDriverByIdNonDeleted(testRequestCar.driverId()))
                    .thenReturn(Optional.of(testDriver));
            when(carRepository.save(testCar)).thenReturn(testCar);
            when(carMapper.carToResponseCar(testCar)).thenReturn(testResponseCar);

            ResponseCar responseCar = carService.addCar(testRequestCar);

            assertEquals(testResponseCar, responseCar);
            verify(carRepository).save(testCar);
            verify(driverRepository).save(testDriver);
        }

        @Test
        void addCarDuplicateLicensePlate() {
            mockCarRepositoryLicensePlateExists(testRequestCar.licensePlate(), true);

            assertThrows(DataIntegrityViolationException.class, () -> carService.addCar(testRequestCar));

            verify(carRepository).existsByLicensePlate(testRequestCar.licensePlate());
            verifyNoMoreInteractions(carRepository, driverRepository, carMapper);
        }

        @Test
        void addCarDriverEntityNotFound() {
            mockCarRepositoryLicensePlateExists(testRequestCar.licensePlate(), false);
            when(driverRepository.findDriverByIdNonDeleted(testRequestCar.driverId()))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.DRIVER_NOT_FOUND.format(testRequestCar.driverId())));

            assertThrows(EntityNotFoundException.class, () -> carService.addCar(testRequestCar));

            verify(carRepository).existsByLicensePlate(testRequestCar.licensePlate());
            verify(carMapper).requestCarToCar(testRequestCar);
            verify(driverRepository).findDriverByIdNonDeleted(testRequestCar.driverId());
            verifyNoMoreInteractions(carRepository, driverRepository, carMapper);
        }
    }

    @Nested
    class EditCarTests {
        @Test
        void editCarOk() {
            Long carId = 1L;
            Car updatedCar = new Car(carId, "newLicensePlate", "newMark", "newColour", testDriver, false);
            ResponseCar expectedResponseCar = new ResponseCar(carId, "newLicensePlate", "newMark", "newColour", testDriver.getId(), false);

            when(carRepository.findCarByIdNonDeleted(carId)).thenReturn(Optional.of(testCar));
            mockCarRepositoryLicensePlateExists("newLicensePlate", false);
            when(carRepository.save(testCar)).thenReturn(updatedCar);
            when(carMapper.carToResponseCar(updatedCar)).thenReturn(expectedResponseCar);

            ResponseCar responseCar = carService.editCar(carId, testRequestCar);

            assertResponseCarEquals(expectedResponseCar, responseCar);
            verify(carRepository).save(testCar);
            verify(carMapper).updateCarFromRequestCar(eq(testRequestCar), eq(testCar));
        }

        @Test
        void editCarEntityNotFound() {
            Long carId = 1L;
            RequestCar requestCar = new RequestCar("newLicensePlate", "newMark", "newColour", 1L);

            when(carRepository.findCarByIdNonDeleted(carId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(carId)));

            assertThrows(EntityNotFoundException.class, () -> carService.editCar(carId, requestCar));

            verify(carRepository).findCarByIdNonDeleted(carId);
            verifyNoInteractions(carMapper);
        }

    }

    @Nested
    class DeleteCarTests {
        @Test
        void deleteCarOk() {
            when(carRepository.findCarByIdNonDeleted(testCar.getId())).thenReturn(Optional.of(testCar));

            carService.deleteCar(testCar.getId());

            verify(carRepository).delete(testCar);
        }

        @Test
        void deleteCarEntityNotFound() {
            when(carRepository.findCarByIdNonDeleted(testCar.getId()))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(testCar.getId())));

            assertThrows(EntityNotFoundException.class, () -> carService.deleteCar(testCar.getId()));

            verify(carRepository).findCarByIdNonDeleted(testCar.getId());
            verifyNoMoreInteractions(carRepository);
        }
    }

    @Nested
    class GetCarTests {
        @Test
        void getCarByIdOk() {
            when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
            when(carMapper.carToResponseCar(testCar)).thenReturn(testResponseCar);

            ResponseCar responseCar = carService.getCarById(testCar.getId());

            assertEquals(testResponseCar, responseCar);
        }

        @Test
        void getCarByIdEntityNotFound() {
            when(carRepository.findById(testCar.getId()))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(testCar.getId())));

            assertThrows(EntityNotFoundException.class, () -> carService.getCarById(testCar.getId()));

            verifyNoMoreInteractions(carMapper);

        }

        @Test
        void getCarByIdNonDeletedOk() {
            when(carRepository.findCarByIdNonDeleted(testCar.getId())).thenReturn(Optional.of(testCar));
            when(carMapper.carToResponseCar(testCar)).thenReturn(testResponseCar);

            ResponseCar responseCar = carService.getCarByIdNonDeleted(testCar.getId());

            assertEquals(testResponseCar, responseCar);
        }

        @Test
        void getCarByIdNonDeletedEntityNotFound() {
            when(carRepository.findCarByIdNonDeleted(testCar.getId()))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(testCar.getId())));

            assertThrows(EntityNotFoundException.class, () -> carService.getCarByIdNonDeleted(testCar.getId()));

            verifyNoMoreInteractions(carMapper);
        }

        @Test
        void getAllCars() {
            Pageable pageable = PageRequest.of(0, 5);
            Page<Car> carPage = new PageImpl<>(List.of(testCar), pageable, 1);

            when(carRepository.findAll(pageable)).thenReturn(carPage);
            when(carMapper.carToResponseCar(testCar)).thenReturn(testResponseCar);

            PagedResponseCarList result = carService.getAllCars(pageable);

            assertPagedResponse(result);

            verify(carRepository).findAll(pageable);
            verify(carMapper).carToResponseCar(testCar);
            verifyNoMoreInteractions(carRepository, carMapper);
        }

        @Test
        void getAllNonDeletedCars() {
            Pageable pageable = PageRequest.of(0, 5);
            Page<Car> carPage = new PageImpl<>(List.of(testCar), pageable, 1);

            when(carRepository.findAllNonDeleted(pageable)).thenReturn(carPage);
            when(carMapper.carToResponseCar(testCar)).thenReturn(testResponseCar);

            PagedResponseCarList result = carService.getAllNonDeletedCars(pageable);

            assertPagedResponse(result);

            verify(carRepository).findAllNonDeleted(pageable);
            verify(carMapper).carToResponseCar(testCar);
            verifyNoMoreInteractions(carRepository, carMapper);
        }
    }

    private void assertResponseCarEquals(ResponseCar expected, ResponseCar actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.licensePlate(), actual.licensePlate());
        assertEquals(expected.mark(), actual.mark());
        assertEquals(expected.colour(), actual.colour());
    }

    private void assertPagedResponse(PagedResponseCarList actual) {
        assertEquals(1, actual.cars().size());
        assertEquals(testResponseCar, actual.cars().get(0));
        assertEquals(1, actual.totalElements());
        assertEquals(1, actual.totalPages());
        assertEquals(0, actual.pageNumber());
        assertEquals(5, actual.pageSize());
        assertTrue(actual.last());
    }

    private void mockCarRepositoryLicensePlateExists(String licensePlate, boolean exists) {
        when(carRepository.existsByLicensePlate(licensePlate)).thenReturn(exists);
    }

}