package com.example.rating_service.repo

import com.example.rating_service.entity.Rate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RateRepository: JpaRepository<Rate, Long> {

    @Query("SELECT r FROM Rate r WHERE r.userType = UserType.PASSENGER")
    fun getAllRatesFromPassengers(pageable: Pageable): Page<Rate>

    @Query("SELECT r FROM Rate r WHERE r.userType = UserType.DRIVER")
    fun getAllRatesFromDrivers(pageable: Pageable): Page<Rate>

    @Query("SELECT r FROM Rate r WHERE r.userId = :id AND r.userType = UserType.PASSENGER")
    fun getAllRatesByPassengerId(id: Long, pageable: Pageable): Page<Rate>

    @Query("SELECT r FROM Rate r WHERE r.userId = :id AND r.userType = UserType.DRIVER")
    fun getAllRatesByDriverId(id: Long, pageable: Pageable): Page<Rate>
}