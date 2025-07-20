package com.uni.cookoff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class CookoffApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// Set env vars manually for Spring Boot
		System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
		System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
		System.setProperty("SPRING_DATASOURCE_DRIVER_CLASS_NAME", dotenv.get("SPRING_DATASOURCE_DRIVER_CLASS_NAME"));
		System.setProperty("judge0.uri", "https://judge0-ce.p.rapidapi.com");
		System.setProperty("judge0.token", "f8d2911b25msh35d67ec84cdbd19p1e70d9jsnce62783e9d10");
		System.setProperty("callback.url", "http://localhost:8080/callback");

		SpringApplication.run(CookoffApplication.class, args);
	}

}
