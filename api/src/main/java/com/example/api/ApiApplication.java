package com.example.api;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@RestController
	static class HelloController {

		@GetMapping("/hello")
		public Map<String, String> hello() {
			return Map.of("message", "Hello, world!");
		}

	}

}
