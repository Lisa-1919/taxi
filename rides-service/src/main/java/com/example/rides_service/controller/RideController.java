package com.example.rides_service.controller;

import com.example.rides_service.dto.RideDto;
import com.example.rides_service.service.RideService;
import com.example.rides_service.util.RideStatuses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    private final RideService rideService;

    @GetMapping("/{id}")
    public ResponseEntity<RideDto> getRideById(@PathVariable Long id) {
        RideDto rideDto = rideService.getRideById(id);
        return ResponseEntity.ok(rideDto);
    }

    @GetMapping
    public ResponseEntity<List<RideDto>> getAllRides() {
        List<RideDto> rideDtoList = rideService.getAllRides();
        return ResponseEntity.ok(rideDtoList);
    }

    @PostMapping
    public ResponseEntity<RideDto> addRide(@Valid @RequestBody RideDto rideDto) {
        RideDto newRideDto = rideService.addRide(rideDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRideDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideDto> editRide(@PathVariable Long id, @Valid @RequestBody RideDto rideDto) {
        RideDto updatedRideDto = rideService.editRide(id, rideDto);
        return ResponseEntity.ok(updatedRideDto);
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<RideDto> updateRideStatus(@PathVariable Long id, @PathVariable("status") RideStatuses newRideStatus) {
        RideDto rideDto = rideService.updateRideStatus(id, newRideStatus);
        return ResponseEntity.ok(rideDto);
    }

}
