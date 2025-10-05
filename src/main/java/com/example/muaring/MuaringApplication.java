package com.example.muaring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MuaringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MuaringApplication.class, args);
	}

}
