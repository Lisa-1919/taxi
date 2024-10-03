package com.example.driver_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.driver_service.dto.DriverDTO;
import com.example.driver_service.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long id){
        DriverDTO driverDTO = driverService.getDriverById(id);
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping
    public ResponseEntity<List<DriverDTO>> getAllDrivers(){
        List<DriverDTO> driverDTOList = driverService.getAllDrivers();
        return ResponseEntity.of(Optional.ofNullable(driverDTOList));
    }

    @PostMapping
    public ResponseEntity<DriverDTO> addDriver(@Validated @RequestBody DriverDTO driverDTO){
        DriverDTO newDriverDTO = driverService.addDriver(driverDTO);
        return ResponseEntity.ok(newDriverDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> editDriver(@PathVariable Long id, @Valid @RequestBody DriverDTO driverDTO) {
        driverDTO.setId(id);
        DriverDTO updatedDriverDTO = driverService.editDriver(driverDTO);
        return ResponseEntity.ok(updatedDriverDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id){
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }


}
