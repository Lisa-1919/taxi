package com.example.rating_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RatingServiceApplication

fun main(args: Array<String>) {
	runApplication<RatingServiceApplication>(*args)
}
