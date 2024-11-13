package com.modsen.passenger.controller;

import com.modsen.passenger.dto.RequestPassenger;
import com.modsen.passenger.dto.ResponsePassenger;
import com.modsen.passenger.dto.PagedResponsePassengerList;
import com.modsen.passenger.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponsePassenger> getPassengerById(
            @PathVariable Long id,
            @RequestParam(value = "active", defaultValue = "true") boolean active
    ) {
        ResponsePassenger passengerDto = active ? passengerService.getPassengerByIdNonDeleted(id)
            : passengerService.getPassengerById(id);
        return ResponseEntity.ok(passengerDto);
    }

    @GetMapping
    public ResponseEntity<PagedResponsePassengerList> getAllPassengers(
            @RequestParam(value = "active", defaultValue = "true") boolean active,
            @RequestParam(value = "page", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        PagedResponsePassengerList pagedResponsePassengerList = active ?
                passengerService.getAllNonDeletedPassengers(PageRequest.of(offset, limit))
                : passengerService.getAllPassengers(PageRequest.of(offset, limit));
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
