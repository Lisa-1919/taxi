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
import com.example.driver_service.utils.CarTestEntityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

    private Car testCar;
    private ResponseCar testResponseCar;
    private RequestCar testRequestCar;
    private Long carId;
    @BeforeEach
    void setUp(){
        carId = CarTestEntityUtils.DEFAULT_CAR_ID;
        testCar = CarTestEntityUtils.createTestCar();
        testResponseCar = CarTestEntityUtils.createTestResponseCar();
        testRequestCar = CarTestEntityUtils.createTestRequestCar();
    }

    @Nested
    class AddCarTests {

        @Test
        void addCarOk() {
            Driver testDriver = CarTestEntityUtils.createTestDriver();
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
            Car updatedCar = CarTestEntityUtils.createUpdatedCar();
            ResponseCar expectedResponseCar = CarTestEntityUtils.createUpdatedResponseCar();

            when(carRepository.findCarByIdNonDeleted(carId)).thenReturn(Optional.of(testCar));
            mockCarRepositoryLicensePlateExists("newLicensePlate", false);
            when(carRepository.save(testCar)).thenReturn(updatedCar);
            when(carMapper.carToResponseCar(updatedCar)).thenReturn(expectedResponseCar);

            ResponseCar responseCar = carService.editCar(carId, testRequestCar);

            assertEquals(expectedResponseCar, responseCar);
            verify(carRepository).save(testCar);
            verify(carMapper).updateCarFromRequestCar(eq(testRequestCar), eq(testCar));
        }

        @Test
        void editCarEntityNotFound() {
            mockCarNotFound(carId);

            assertThrows(EntityNotFoundException.class, () -> carService.editCar(carId, any(RequestCar.class)));

            verify(carRepository).findCarByIdNonDeleted(carId);
            verifyNoInteractions(carMapper);
        }

    }

    @Nested
    class DeleteCarTests {
        @Test
        void deleteCarOk() {
            when(carRepository.findCarByIdNonDeleted(carId)).thenReturn(Optional.of(testCar));

            carService.deleteCar(carId);

            verify(carRepository).delete(testCar);
        }

        @Test
        void deleteCarEntityNotFound() {
            mockCarNotFound(carId);

            assertThrows(EntityNotFoundException.class, () -> carService.deleteCar(carId));

            verify(carRepository).findCarByIdNonDeleted(carId);
            verifyNoMoreInteractions(carRepository);
        }
    }

    @Nested
    class GetCarTests {
        @Test
        void getCarByIdOk() {
            when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
            when(carMapper.carToResponseCar(testCar)).thenReturn(testResponseCar);

            ResponseCar responseCar = carService.getCarById(carId);

            assertEquals(testResponseCar, responseCar);
        }

        @Test
        void getCarByIdEntityNotFound() {
            when(carRepository.findById(carId))
                    .thenThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(carId)));

            assertThrows(EntityNotFoundException.class, () -> carService.getCarById(carId));

            verifyNoMoreInteractions(carMapper);

        }

        @Test
        void getCarByIdNonDeletedOk() {
            when(carRepository.findCarByIdNonDeleted(carId)).thenReturn(Optional.of(testCar));
            when(carMapper.carToResponseCar(testCar)).thenReturn(testResponseCar);

            ResponseCar responseCar = carService.getCarByIdNonDeleted(carId);

            assertEquals(testResponseCar, responseCar);
        }

        @Test
        void getCarByIdNonDeletedEntityNotFound() {
            mockCarNotFound(carId);

            assertThrows(EntityNotFoundException.class, () -> carService.getCarByIdNonDeleted(carId));

            verifyNoMoreInteractions(carMapper);
        }

        @Test
        void getAllCars() {
            PageRequest pageable = CarTestEntityUtils.createDefaultPageRequest();
            Page<Car> carPage = CarTestEntityUtils.createDefaultCarPage(List.of(testCar));

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
            PageRequest pageable = CarTestEntityUtils.createDefaultPageRequest();
            Page<Car> carPage = CarTestEntityUtils.createDefaultCarPage(List.of(testCar));

            when(carRepository.findAllNonDeleted(pageable)).thenReturn(carPage);
            when(carMapper.carToResponseCar(testCar)).thenReturn(testResponseCar);

            PagedResponseCarList result = carService.getAllNonDeletedCars(pageable);

            assertPagedResponse(result);

            verify(carRepository).findAllNonDeleted(pageable);
            verify(carMapper).carToResponseCar(testCar);
            verifyNoMoreInteractions(carRepository, carMapper);
        }
    }

    private void assertPagedResponse(PagedResponseCarList actual) {
        assertEquals(testResponseCar, actual.cars().get(0));
        assertEquals(CarTestEntityUtils.DEFAULT_TOTAL_ELEMENTS, actual.totalElements());
        assertEquals(CarTestEntityUtils.DEFAULT_TOTAL_PAGES, actual.totalPages());
        assertEquals(CarTestEntityUtils.DEFAULT_PAGE_NUMBER, actual.pageNumber());
        assertEquals(CarTestEntityUtils.DEFAULT_PAGE_SIZE, actual.pageSize());
        assertTrue(actual.last());
    }

    private void mockCarRepositoryLicensePlateExists(String licensePlate, boolean exists) {
        when(carRepository.existsByLicensePlate(licensePlate)).thenReturn(exists);
    }

    private void mockCarNotFound(Long id){
        when(carRepository.findCarByIdNonDeleted(id))
                .thenThrow(new EntityNotFoundException(ExceptionMessages.CAR_NOT_FOUND.format(id)));
    }
}