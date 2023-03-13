package com.challenge.tenpo.sumador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SumApplication {

	public static void main(String[] args) {
		SpringApplication.run(SumApplication.class, args);
	}

}
