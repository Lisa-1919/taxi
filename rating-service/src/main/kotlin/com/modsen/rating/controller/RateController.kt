package com.modsen.rating.controller

import com.modsen.exception_handler.dto.ErrorResponse
import com.modsen.exception_handler.exception.ValidationErrorResponse
import com.modsen.rating.dto.PagedResponseRateList
import com.modsen.rating.dto.RequestRate
import com.modsen.rating.dto.ResponseRate
import com.modsen.rating.service.RateService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import java.util.*
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
open class RateController(
    private val rateService: RateService
) {

    @Operation(summary = "Get the rate", description = "Returns the rate")
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "Successfully retrieved",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ResponseRate::class))]),
            ApiResponse(responseCode = "400", description = "Bad Request",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ValidationErrorResponse::class))]),
            ApiResponse(responseCode = "404", description = "Not Found",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "401", description = "Unauthorized",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "403", description = "Forbidden",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponse::class))])
        ])
    @GetMapping("/{id}")
    open fun getRateById(@PathVariable id: Long): ResponseEntity<ResponseRate> {
        val responseRate = rateService.getRateById(id)
        return ResponseEntity.ok(responseRate)
    }

    @Operation(summary = "Get all rates", description = "Returns all the rates")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved",
        content = [Content(mediaType = "application/json",
            schema = Schema(implementation = PagedResponseRateList::class))]),
        ApiResponse(responseCode = "401", description = "Unauthorized",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "403", description = "Forbidden",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))])
    ])
    @GetMapping
    open fun getAllRates(
        @PageableDefault(page = 0, size = 10) pageable: Pageable
    ): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRates(pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @Operation(summary = "Get all rates", description = "Returns all the rates")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = PagedResponseRateList::class))]),
        ApiResponse(responseCode = "401", description = "Unauthorized",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "403", description = "Forbidden",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))])
    ])
    @GetMapping("/from-passengers")
    open fun getAllRatesFromPassengers(
        @PageableDefault(page = 0, size = 10) pageable: Pageable
    ): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRatesFromPassengers(pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @Operation(summary = "Get all rates", description = "Returns all the rates")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = PagedResponseRateList::class))]),
        ApiResponse(responseCode = "401", description = "Unauthorized",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "403", description = "Forbidden",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))])
    ])
    @GetMapping("/from-passengers/{passengerId}")
    open fun getAllRatesByPassengerId(
        @PathVariable passengerId: UUID,
        @PageableDefault(page = 0, size = 10) pageable: Pageable
    ): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRatesByPassengerId(passengerId, pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @Operation(summary = "Get all rates", description = "Returns all the rates")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = PagedResponseRateList::class))]),
        ApiResponse(responseCode = "401", description = "Unauthorized",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "403", description = "Forbidden",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))])
    ])
    @GetMapping("/from-drivers")
    open fun getAllRatesFromDrivers(
        @PageableDefault(page = 0, size = 10) pageable: Pageable
    ): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRatesFromDrivers(pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @Operation(summary = "Get all rates", description = "Returns all the rates")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = PagedResponseRateList::class))]),
        ApiResponse(responseCode = "401", description = "Unauthorized",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "403", description = "Forbidden",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ErrorResponse::class))])
    ])
    @GetMapping("/from-drivers/{driverId}")
    open fun getAllRatesByDriverId(
        @PathVariable driverId: UUID,
        @PageableDefault(page = 0, size = 10) pageable: Pageable
    ): ResponseEntity<PagedResponseRateList> {
        val responseRateList = rateService.getAllRatesByDriverId(driverId, pageable)
        return ResponseEntity.ok(responseRateList)
    }

    @Operation(summary = "Add a rate", description = "Returns a new rate")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Successfully created",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ResponseRate::class))]),
            ApiResponse(responseCode = "400", description = "Bad Request",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = ValidationErrorResponse::class))]),
            ApiResponse(responseCode = "404", description = "Not Found",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "409",description = "Conflict",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "401", description = "Unauthorized",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponse::class))]),
            ApiResponse(responseCode = "403", description = "Forbidden",
                content = [Content(mediaType = "application/json",
                    schema = Schema(implementation = ErrorResponse::class))])
        ])
    @PostMapping
    open fun addRate(@RequestBody requestRate: RequestRate): ResponseEntity<ResponseRate> {
        val responseRate = rateService.addRate(requestRate)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseRate)
    }

}