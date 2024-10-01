package com.example.driver_service.service;

import com.example.driver_service.dto.CarDTO;
import com.example.driver_service.entity.Car;
import com.example.driver_service.mapper.CarMapper;
import com.example.driver_service.repo.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService{

    private final CarRepository carRepository;
    private final CarMapper carMapper;


    @Override
    public CarDTO addCar(CarDTO carDTO) {
        Car car = carMapper.carDTOToCar(carDTO);
        car = carRepository.save(car);
        return carMapper.carToCarDTO(car);
    }

    @Override
    public CarDTO editCar(CarDTO carDTO) {
        Car carFromDB = carRepository.findById(carDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        carMapper.updateCarFromCarDTO(carDTO, carFromDB);

        return carMapper.carToCarDTO(carRepository.save(carFromDB));
    }

    @Override
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        carRepository.delete(car);
    }

    @Override
    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        return carMapper.carToCarDTO(car);
    }

    @Override
    public List<CarDTO> getAllCars() {
        List<Car> cars = carRepository.findAll();

        return cars.stream()
                .map(carMapper::carToCarDTO)
                .collect(Collectors.toList());
    }
}
