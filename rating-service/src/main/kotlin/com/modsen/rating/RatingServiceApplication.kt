package com.modsen.rating

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
open class RatingServiceApplication

fun main(args: Array<String>) {
	runApplication<com.modsen.rating.RatingServiceApplication>(*args)
}
