package com.modsen.driver.controller;

import com.modsen.driver.dto.RequestCar;
import com.modsen.driver.dto.ResponseCar;
import com.modsen.driver.dto.PagedResponseCarList;
import com.modsen.driver.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<PagedResponseCarList> getAllNonDeletedCars(@PageableDefault(page = 0, size = DEFAULT_SIZE) Pageable pageable) {
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
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
