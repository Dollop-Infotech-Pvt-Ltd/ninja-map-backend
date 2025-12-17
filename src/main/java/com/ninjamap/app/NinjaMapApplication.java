package com.ninjamap.app;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class NinjaMapApplication {

	public static void main(String[] args) {
		// Force JVM timezone before Hibernate initializes
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
//		System.out.println("JVM TimeZone: " + TimeZone.getDefault().getID());

		SpringApplication.run(NinjaMapApplication.class, args);
	}

}
