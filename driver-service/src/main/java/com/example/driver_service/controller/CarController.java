package com.example.driver_service.controller;

import com.example.driver_service.dto.RequestCar;
import com.example.driver_service.dto.ResponseCar;
import com.example.driver_service.dto.ResponseCarList;
import com.example.driver_service.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCar> getCarById(@PathVariable Long id) {
        ResponseCar responseCar = carService.getCarById(id);
        return ResponseEntity.ok(responseCar);
    }

    @GetMapping
    public ResponseEntity<ResponseCarList> getAllCars() {
        ResponseCarList responseCarList = carService.getAllCars();
        return ResponseEntity.ok(responseCarList);
    }

    @PostMapping
    public ResponseEntity<ResponseCar> addCar(@Valid @RequestBody RequestCar requestCar) {
        ResponseCar responseCar = carService.addCar(requestCar);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseCar);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseCar> editCar(@PathVariable Long id, @Valid @RequestBody RequestCar requestCar) {
        ResponseCar responseCar = carService.editCar(id, requestCar);
        return ResponseEntity.ok(responseCar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }
}
