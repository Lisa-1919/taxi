package com.example.driver_service.controller;

import com.example.driver_service.dto.DriverDto;
import com.example.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/{id}")
    public ResponseEntity<DriverDto> getDriverById(@PathVariable Long id){
        DriverDto driverDTO = driverService.getDriverById(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping
    public ResponseEntity<List<DriverDto>> getAllDrivers(){
        List<DriverDto> driverDtoList = driverService.getAllDrivers();
        return ResponseEntity.ok(driverDtoList);
    }

    @PostMapping
    public ResponseEntity<DriverDto> addDriver(@Validated @RequestBody DriverDto driverDto){
        DriverDto newDriverDto = driverService.addDriver(driverDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDriverDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDto> editDriver(@PathVariable Long id, @Valid @RequestBody DriverDto driverDto) {
        DriverDto updatedDriverDto = driverService.editDriver(id, driverDto);
        return ResponseEntity.ok(updatedDriverDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id){
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }


}
