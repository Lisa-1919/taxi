package com.example.passenger_service.controller;

import com.example.passenger_service.dto.PassengerDto;
import com.example.passenger_service.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDto> getPassengerById(@PathVariable Long id) {
        PassengerDto passengerDto = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passengerDto);
    }

    @GetMapping
    public ResponseEntity<List<PassengerDto>> getAllPassengers() {
        List<PassengerDto> passengerDtoList = passengerService.getAllPassengers();
        return ResponseEntity.ok(passengerDtoList);
    }

    @PostMapping
    public ResponseEntity<PassengerDto> addPassenger(@Valid @RequestBody PassengerDto passengerDto) {
        PassengerDto newPassengerDto = passengerService.addPassenger(passengerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPassengerDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerDto> editPassenger(@PathVariable Long id, @Valid @RequestBody PassengerDto passengerDto) {
        PassengerDto updatedPassengerDto = passengerService.editPassenger(id, passengerDto);
        return ResponseEntity.ok(updatedPassengerDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.ok().build();
    }

}
