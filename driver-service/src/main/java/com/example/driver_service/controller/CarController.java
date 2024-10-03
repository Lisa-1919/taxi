package com.example.driver_service.controller;

import com.example.driver_service.dto.CarDTO;
import com.example.driver_service.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getCarById(@PathVariable Long id) {
        CarDTO carDTO = carService.getCarById(id);
        return ResponseEntity.ok(carDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllCars() {
        List<CarDTO> carDTOList = carService.getAllCars();
        return ResponseEntity.ok(carDTOList);
    }

    @PostMapping
    public ResponseEntity<?> addCar(@RequestBody CarDTO carDTO) {
        CarDTO newCarDTO = carService.addCar(carDTO);
        return ResponseEntity.ok(newCarDTO);
    }

    @PutMapping
    public ResponseEntity<?> editCar(@RequestBody CarDTO carDTO){
        CarDTO updatedCarDTO = carService.editCar(carDTO);
        return ResponseEntity.ok(updatedCarDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id){
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }
}
