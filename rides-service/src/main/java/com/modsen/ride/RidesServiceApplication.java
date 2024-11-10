package com.modsen.ride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.modsen.ride.client")
public class RidesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RidesServiceApplication.class, args);
	}

}
