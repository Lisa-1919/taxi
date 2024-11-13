package com.modsen.passenger.controller;

import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.dto.PagedResponsePassengerList;
import com.modsen.passenger.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/passengers")
public class PassengerController {

    private final static int DEFAULT_SIZE = 10;
    private final PassengerService passengerService;

    @GetMapping("/all{id}")
    public ResponseEntity<ResponsePassenger> getPassengerById(@PathVariable Long id) {
        ResponsePassenger passengerDto = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passengerDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsePassenger> getPassengerByIdNonDeleted(@PathVariable Long id) {
        ResponsePassenger passengerDto = passengerService.getPassengerByIdNonDeleted(id);
        return ResponseEntity.ok(passengerDto);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponsePassengerList> getAllPassengers(@PageableDefault(page = 0, size = DEFAULT_SIZE) Pageable pageable) {
        PagedResponsePassengerList pagedResponsePassengerList = passengerService.getAllPassengers(pageable);
        return ResponseEntity.ok(pagedResponsePassengerList);
    }

    @GetMapping
    public ResponseEntity<PagedResponsePassengerList> getAllNonDeletedPassengers(@PageableDefault(page = 0, size = DEFAULT_SIZE) Pageable pageable) {
        PagedResponsePassengerList pagedResponsePassengerList = passengerService.getAllNonDeletedPassengers(pageable);
        return ResponseEntity.ok(pagedResponsePassengerList);
    }

    @PostMapping
    public ResponseEntity<ResponsePassenger> addPassenger(@Valid @RequestBody RequestPassenger requestPassenger) {
        ResponsePassenger responsePassenger = passengerService.addPassenger(requestPassenger);
        return ResponseEntity.status(HttpStatus.CREATED).body(responsePassenger);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePassenger> editPassenger(@PathVariable Long id, @Valid @RequestBody RequestPassenger requestPassenger) {
        ResponsePassenger responsePassenger = passengerService.editPassenger(id, requestPassenger);
        return ResponseEntity.ok(responsePassenger);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> doesPassengerExist(@PathVariable Long id) {
        boolean exists = passengerService.doesPassengerExist(id);
        return ResponseEntity.ok(exists);
    }

}
