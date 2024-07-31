package com.blue.getout;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GetoutApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetoutApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(){
		return runner-> System.out.println("hi");
	}
}
