package com.ninjamap.app.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurer() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		registerPublicOrigins(source);
		return source;
	}

	private void registerPublicOrigins(UrlBasedCorsConfigurationSource source) {
		CorsConfiguration publicCorsConfig = new CorsConfiguration();

		// Set the specific origin that is allowed
		publicCorsConfig.setAllowedOriginPatterns(List.of("*"));

		// Allow all methods
		publicCorsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

		// Allow specific headers
		publicCorsConfig
				.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-XSRF-TOKEN", "XSRF-TOKEN"));
		// Allow credentials (set to true for cookie-based sessions)
		publicCorsConfig.setAllowCredentials(true);

		// Register the CORS configuration for all endpoints
		source.registerCorsConfiguration("/**", publicCorsConfig);
	}
}
