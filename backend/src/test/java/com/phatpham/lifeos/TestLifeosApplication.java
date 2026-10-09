package com.phatpham.lifeos;

import org.springframework.boot.SpringApplication;

public class TestLifeosApplication {

	public static void main(String[] args) {
		SpringApplication.from(LifeosApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
