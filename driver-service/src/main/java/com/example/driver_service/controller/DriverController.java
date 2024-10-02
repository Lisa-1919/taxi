package com.example.driver_service.controller;

import com.example.driver_service.dto.DriverDTO;
import com.example.driver_service.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/{id}")
    private ResponseEntity<?> getDriverById(@PathVariable Long id){
        DriverDTO driverDTO = driverService.getDriverById(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping
    private ResponseEntity<?> getAllDrivers(){
        List<DriverDTO> driverDTOList = driverService.getAllDrivers();
        return ResponseEntity.of(Optional.ofNullable(driverDTOList));
    }

    @PostMapping
    private ResponseEntity<?> addDriver(@RequestBody DriverDTO driverDTO){
        DriverDTO newDriverDTO = driverService.addDriver(driverDTO);
        return ResponseEntity.ok(newDriverDTO);
    }

    @PutMapping("/{id}")
    private ResponseEntity<?> editDriver(@PathVariable Long id, @RequestBody DriverDTO driverDTO) {
        driverDTO.setId(id);
        DriverDTO updatedDriverDTO = driverService.editDriver(driverDTO);
        return ResponseEntity.ok(updatedDriverDTO);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> deleteDriver(@PathVariable Long id){
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }


}
