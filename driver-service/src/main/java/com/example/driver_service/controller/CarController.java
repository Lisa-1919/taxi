package com.example.driver_service.controller;

import com.example.driver_service.dto.RequestCar;
import com.example.driver_service.dto.ResponseCar;
import com.example.driver_service.dto.PagedResponseCarList;
import com.example.driver_service.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final int DEFAULT_SIZE = 10;
    private final CarService carService;

    @GetMapping("/all/{id}")
    public ResponseEntity<ResponseCar> getCarById(@PathVariable Long id) {
        ResponseCar responseCar = carService.getCarById(id);
        return ResponseEntity.ok(responseCar);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCar> getCarByIdNonDeleted(@PathVariable Long id) {
        ResponseCar responseCar = carService.getCarByIdNonDeleted(id);
        return ResponseEntity.ok(responseCar);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponseCarList> getAllCars(@PageableDefault(page = 0, size = DEFAULT_SIZE) Pageable pageable) {
        PagedResponseCarList pagedResponseCarList = carService.getAllCars(pageable);
        return ResponseEntity.ok(pagedResponseCarList);
    }

    @GetMapping
    public ResponseEntity<PagedResponseCarList> getAllNonDeletedCars(@PageableDefault(page = 0, size = DEFAULT_SIZE) Pageable pageable){
        PagedResponseCarList pagedResponseCarList = carService.getAllNonDeletedCars(pageable);
        return ResponseEntity.ok(pagedResponseCarList);
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
