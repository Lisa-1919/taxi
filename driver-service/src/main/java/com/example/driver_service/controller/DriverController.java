package com.example.driver_service.controller;

import com.example.driver_service.dto.RequestDriver;
import com.example.driver_service.dto.ResponseDriver;
import com.example.driver_service.dto.ResponseDriverList;
import com.example.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDriver> getDriverById(@PathVariable Long id){
        ResponseDriver driverDTO = driverService.getDriverById(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping
    public ResponseEntity<ResponseDriverList> getAllDrivers(){
        ResponseDriverList responseDriverList = driverService.getAllDrivers();
        return ResponseEntity.ok(responseDriverList);
    }

    @PostMapping
    public ResponseEntity<ResponseDriver> addDriver(@Validated @RequestBody RequestDriver requestDriver){
        ResponseDriver responseDriver = driverService.addDriver(requestDriver);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDriver);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDriver> editDriver(@PathVariable Long id, @Valid @RequestBody RequestDriver requestDriver) {
        ResponseDriver responseDriver = driverService.editDriver(id, requestDriver);
        return ResponseEntity.ok(responseDriver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id){
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }

}
