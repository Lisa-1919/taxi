package com.example.driver_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.driver_service.dto.DriverDto;
import com.example.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

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
        return ResponseEntity.of(Optional.ofNullable(driverDtoList));
    }

    @PostMapping
    public ResponseEntity<DriverDto> addDriver(@Validated @RequestBody DriverDto driverDto){
        DriverDto newDriverDto = driverService.addDriver(driverDto);
        return ResponseEntity.ok(newDriverDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDto> editDriver(@PathVariable Long id, @Valid @RequestBody DriverDto driverDto) {
        driverDto.setId(id);
        DriverDto updatedDriverDto = driverService.editDriver(driverDto);
        return ResponseEntity.ok(updatedDriverDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id){
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }


}
