package com.example.driver_service.controller;

import com.example.driver_service.dto.PagedResponseDriverList;
import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final int DEFAULT_SIZE = 10;
    private final DriverService driverService;

    @GetMapping("/all/{id}")
    public ResponseEntity<ResponseDriver> getDriverById(@PathVariable Long id) {
        ResponseDriver driverDTO = driverService.getDriverById(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDriver> getDriverByIdNonDeleted(@PathVariable Long id) {
        ResponseDriver driverDTO = driverService.getDriverByIdNonDeleted(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponseDriverList> getAllDrivers(@PageableDefault(page = 0, size = DEFAULT_SIZE) Pageable pageable) {
        PagedResponseDriverList allDrivers = driverService.getAllDrivers(pageable);
        return ResponseEntity.ok(allDrivers);
    }

    @GetMapping
    public ResponseEntity<PagedResponseDriverList> getAllNonDeletedDrivers(@PageableDefault(page = 0, size = DEFAULT_SIZE) Pageable pageable) {
        PagedResponseDriverList allNonDeletedDrivers = driverService.getAllNonDeletedDrivers(pageable);
        return ResponseEntity.ok(allNonDeletedDrivers);
    }

    @PostMapping
    public ResponseEntity<ResponseDriver> addDriver(@Validated @RequestBody RequestDriver requestDriver) {
        ResponseDriver responseDriver = driverService.addDriver(requestDriver);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDriver);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDriver> editDriver(@PathVariable Long id, @Valid @RequestBody RequestDriver requestDriver) {
        ResponseDriver responseDriver = driverService.editDriver(id, requestDriver);
        return ResponseEntity.ok(responseDriver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> doesDriverExist(@PathVariable Long id) {
        boolean exists = driverService.doesDriverExist(id);
        return ResponseEntity.ok(exists);
    }

}
