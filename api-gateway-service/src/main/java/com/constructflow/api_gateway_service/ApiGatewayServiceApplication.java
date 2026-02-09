package com.constructflow.api_gateway_service;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class ApiGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayServiceApplication.class, args);
	}

    @PostConstruct
    public void init() {
        // Option B: As a post-construct (sometimes more reliable in certain SB3 versions)
        Hooks.enableAutomaticContextPropagation();
    }

}
