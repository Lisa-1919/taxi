package com.example.driver_service.controller;

import com.example.driver_service.dto.CarDto;
import com.example.driver_service.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/{id}")
    public ResponseEntity<CarDto> getCarById(@PathVariable Long id) {
        CarDto carDto = carService.getCarById(id);
        return ResponseEntity.ok(carDto);
    }

    @GetMapping
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> carDtoList = carService.getAllCars();
        return ResponseEntity.ok(carDtoList);
    }

    @PostMapping
    public ResponseEntity<CarDto> addCar(@Valid @RequestBody CarDto carDto) {
        CarDto newCarDto = carService.addCar(carDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCarDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDto> editCar(@PathVariable Long id, @Valid @RequestBody CarDto carDto) {
        CarDto updatedCarDto = carService.editCar(id, carDto);
        return ResponseEntity.ok(updatedCarDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }
}
