package com.modsen.ride.controller;

import com.modsen.ride.dto.PagedResponseRideList;
import com.modsen.ride.dto.RequestChangeStatus;
import com.modsen.ride.dto.RequestRide;
import com.modsen.ride.dto.ResponseRide;
import com.modsen.ride.service.RideService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    private final RideService rideService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseRide> getRideById(@PathVariable Long id) {
        ResponseRide responseRide = rideService.getRideById(id);
        return ResponseEntity.ok(responseRide);
    }

    @GetMapping
    public ResponseEntity<PagedResponseRideList> getAllRides(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        PagedResponseRideList pagedResponseRideList = rideService.getAllRides(page, limit);
        return ResponseEntity.ok(pagedResponseRideList);
    }

    @PostMapping
    public ResponseEntity<ResponseRide> addRide(@Valid @RequestBody RequestRide requestRide) {
        ResponseRide responseRide = rideService.addRide(requestRide);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseRide);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseRide> editRide(@PathVariable Long id, @Valid @RequestBody RequestRide requestRide) {
        ResponseRide responseRide = rideService.editRide(id, requestRide);
        return ResponseEntity.ok(responseRide);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseRide> updateRideStatus(@PathVariable Long id, @RequestBody RequestChangeStatus requestChangeStatus) {
        ResponseRide responseRide = rideService.updateRideStatus(id, requestChangeStatus);
        return ResponseEntity.ok(responseRide);
    }

    @GetMapping("/{id}/driver/{driverId}/exists")
    public ResponseEntity<Boolean> doesRideExistForDriver(@PathVariable("id") Long id, @PathVariable("driverId") UUID driverId) {
        Boolean exists = rideService.doesRideExistForDriver(id, driverId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/passenger/{passengerId}/exists")
    public ResponseEntity<Boolean> doesRideExistForPassenger(@PathVariable("id") Long id, @PathVariable("passengerId") UUID passengerId) {
        Boolean exists = rideService.doesRideExistForPassenger(id, passengerId);
        return ResponseEntity.ok(exists);
    }

}
