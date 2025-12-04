package com.example.QuattroTech.shop;

import org.springframework.boot.SpringApplication;

public class TestQuattroTechApplication {

	public static void main(String[] args) {
		SpringApplication.from(QuattroTechApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
