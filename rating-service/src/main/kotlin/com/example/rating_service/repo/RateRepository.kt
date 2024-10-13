package com.example.rating_service.repo

import com.example.rating_service.entity.Rate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RateRepository: JpaRepository<Rate, Long>