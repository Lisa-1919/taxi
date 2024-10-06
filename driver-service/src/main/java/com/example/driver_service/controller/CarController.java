package com.example.driver_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.driver_service.dto.CarDto;
import com.example.driver_service.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        CarDto carDto = carService.getCarById(id);
        return ResponseEntity.ok(carDto);
    }

    @GetMapping
    public ResponseEntity<?> getAllCars() {
        List<CarDto> carDtoList = carService.getAllCars();
        return ResponseEntity.ok(carDtoList);
    }

    @PostMapping
    public ResponseEntity<?> addCar(@Valid @RequestBody CarDto carDto) {
        CarDto newCarDto = carService.addCar(carDto);
        return ResponseEntity.ok(newCarDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editCar(@PathVariable Long id, @Valid  @RequestBody CarDto carDto){
        CarDto updatedCarDto = carService.editCar(carDto);
        return ResponseEntity.ok(updatedCarDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id){
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }
}
