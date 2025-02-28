package com.modsen.driver.controller;

import com.modsen.driver.dto.CreateDriverRequest;
import com.modsen.driver.dto.PagedResponseDriverList;
import com.modsen.driver.dto.RequestDriver;
import com.modsen.driver.dto.ResponseDriver;
import com.modsen.driver.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDriver> getDriverByIdNonDeleted(
            @PathVariable UUID id,
            @RequestParam(value = "active", defaultValue = "true") boolean active
    ) {
        ResponseDriver driverDTO = active ? driverService.getDriverByIdNonDeleted(id)
                : driverService.getDriverById(id);
        System.out.println("Create a driver with id " + driverDTO.id());
        return ResponseEntity.ok(driverDTO);
    }

    @GetMapping
    public ResponseEntity<PagedResponseDriverList> getAllNonDeletedDrivers(
            @RequestParam(value = "active", defaultValue = "true") boolean active,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit
    ) {
        PagedResponseDriverList allNonDeletedDrivers = active ? driverService.getAllNonDeletedDrivers(PageRequest.of(page, limit))
                : driverService.getAllDrivers(PageRequest.of(page, limit));
        return ResponseEntity.ok(allNonDeletedDrivers);
    }

    @PostMapping
    public ResponseEntity<ResponseDriver> addDriver(@Validated @RequestBody CreateDriverRequest createDriverRequest) {
        ResponseDriver responseDriver = driverService.addDriver(createDriverRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDriver);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDriver> editDriver(@PathVariable UUID id, @Valid @RequestBody RequestDriver requestDriver) {
        ResponseDriver responseDriver = driverService.editDriver(id, requestDriver);
        return ResponseEntity.ok(responseDriver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID id) {
        driverService.deleteDriver(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> doesDriverExist(@PathVariable UUID id) {
        boolean exists = driverService.doesDriverExist(id);
        return ResponseEntity.ok(exists);
    }

}
