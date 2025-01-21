package com.modsen.rating.controller

import com.modsen.rating.dto.RequestRate
import com.modsen.rating.dto.ResponseRate
import com.modsen.rating.dto.PagedResponseRateList
import com.modsen.rating.service.RateService
import java.util.UUID
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/rates")
class RateController(
    private val rateService: RateService
) {

    @GetMapping("/{id}")
    fun getRateById(@PathVariable id: Long): ResponseEntity<ResponseRate> {
        val responseRate = rateService.getRateById(id)
        return ResponseEntity.ok(responseRate)
    }

    @GetMapping
    fun getAllRates(@PageableDefault(page = 0, size = 10) pageable: Pageable): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRates(pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @GetMapping("/from-passengers")
    fun getAllRatesFromPassengers(@PageableDefault(page = 0, size = 10) pageable: Pageable): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRatesFromPassengers(pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @GetMapping("/from-passengers/{passengerId}")
    fun getAllRatesByPassengerId(@PathVariable passengerId: UUID, @PageableDefault(page = 0, size = 10) pageable: Pageable): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRatesByPassengerId(passengerId, pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @GetMapping("/from-drivers")
    fun getAllRatesFromDrivers(@PageableDefault(page = 0, size = 10) pageable: Pageable): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRatesFromDrivers(pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @GetMapping("/from-drivers/{driverId}")
    fun getAllRatesByDriverId(@PathVariable driverId: UUID, @PageableDefault(page = 0, size = 10) pageable: Pageable): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRatesByDriverId(driverId, pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @PostMapping
    fun addRate(@RequestBody requestRate: RequestRate): ResponseEntity<ResponseRate> {
        val responseRate = rateService.addRate(requestRate)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseRate)
    }

}