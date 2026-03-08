package com.stayFinder.proyectoFinal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GestionDeReservaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDeReservaApplication.class, args);
	}

}
