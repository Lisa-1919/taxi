package com.modsen.driver.controller;

import com.modsen.driver.dto.RequestCar;
import com.modsen.driver.dto.ResponseCar;
import com.modsen.driver.dto.PagedResponseCarList;
import com.modsen.driver.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCar> getCarById(
            @PathVariable Long id,
            @RequestParam(value = "active", defaultValue = "true") boolean active) {
        ResponseCar responseCar = active ? carService.getCarByIdNonDeleted(id)
                : carService.getCarById(id);
        return ResponseEntity.ok(responseCar);
    }

    @GetMapping
    public ResponseEntity<PagedResponseCarList> getAllCars(
            @RequestParam(value = "active", defaultValue = "true") boolean active,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        PagedResponseCarList pagedResponseCarList = active ? carService.getAllNonDeletedCars(PageRequest.of(page, limit))
                : carService.getAllCars(PageRequest.of(page, limit));
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
