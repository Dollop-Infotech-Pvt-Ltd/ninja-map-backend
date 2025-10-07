package com.ninjamap.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class NinjaMapApplication {

	public static void main(String[] args) {
		SpringApplication.run(NinjaMapApplication.class, args);
	}

}
