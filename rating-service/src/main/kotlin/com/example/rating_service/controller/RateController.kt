package com.example.rating_service.controller

import com.example.rating_service.dto.RequestRate
import com.example.rating_service.dto.ResponseRate
import com.example.rating_service.dto.ResponseRateList
import com.example.rating_service.service.RateService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/rates")
class RateController(
    private val rateService: RateService
) {

    @GetMapping("/{id}")
    fun getRateById(@PathVariable id: Long): ResponseEntity<ResponseRate>{
        val responseRate = rateService.getRateById(id)
        return ResponseEntity.ok(responseRate)
    }

    @GetMapping
    fun getAllRates(): ResponseEntity<ResponseRateList>{
        val responseRateList = rateService.getAllRates()
        return ResponseEntity.ok(responseRateList)
    }

    @PostMapping
    fun addRate(@RequestBody requestRate: RequestRate) :ResponseEntity<ResponseRate>{
        val responseRate = rateService.addRate(requestRate)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseRate)
    }

}