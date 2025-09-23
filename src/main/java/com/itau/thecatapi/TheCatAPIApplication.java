package com.itau.thecatapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TheCatAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheCatAPIApplication.class, args);
	}

}