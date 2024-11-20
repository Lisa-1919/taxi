package com.modsen.rating

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
open class RatingServiceApplication

fun main(args: Array<String>) {
	runApplication<com.modsen.rating.RatingServiceApplication>(*args)
}
