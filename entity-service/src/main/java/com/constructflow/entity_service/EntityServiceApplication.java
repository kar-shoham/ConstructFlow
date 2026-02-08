package com.constructflow.entity_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EntityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EntityServiceApplication.class, args);
	}

}
