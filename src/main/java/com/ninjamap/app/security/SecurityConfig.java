package com.ninjamap.app.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ninjamap.app.config.CorsConfig;
import com.ninjamap.app.security.filter.IpRateLimitFilter;
import com.ninjamap.app.security.filter.SecurityFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final SecurityFilter jwtFilter;
	private final IpRateLimitFilter ipRateLimitFilter;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final CustomAccessDeniedHandler accessDeniedHandler;
	private final CorsConfig corsConfig;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomOAuth2FailureHandler customOAuth2FailureHandler;

	private static final List<String> PUBLIC_URLS = List.of("/api/auth/**", "/api/admin/auth/**", "/actuator/**",
			"/api/about-us/get", "/api/contact-us/submit", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
			"/v2/api-docs/**", "/swagger-resources/**", "/webjars/**", "/api/faqs/get", "/api/faqs/get-all",
			"/api/comments/**", "/api/blogs/get", "/api/blogs/get-all", "/api/policies/get", "/api/policies/get-all",
			"/oauth2/authorization/google");

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		System.err.println("----------------------------------------");

		return http.csrf(csrf -> csrf.disable()) // Disable CSRF
		// CSRF with cookie repository; ignore public URLs
//				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//						.ignoringRequestMatchers("/api/auth/csrf", "/api/auth/**", "/api/admin/auth/**",
//								"/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v2/api-docs/**",
//								"/swagger-resources/**", "/webjars/**", "/actuator/**")
//						.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
				// CORS configuration
				.cors(cors -> cors.configurationSource(corsConfig.corsConfigurer()))
				// Exception handling
				.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint)
						.accessDeniedHandler(accessDeniedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Authorization rules
				.authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_URLS.toArray(new String[0])).permitAll()
						.anyRequest().authenticated())
				.oauth2Login(oauth -> oauth.loginPage("/oauth2/authorization/google")
						.successHandler(customOAuth2SuccessHandler).failureHandler(customOAuth2FailureHandler))
				// JWT filter before username/password auth
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
