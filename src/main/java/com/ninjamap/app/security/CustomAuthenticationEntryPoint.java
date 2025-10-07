package com.ninjamap.app.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjamap.app.payload.response.ErrorResponse;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		authException.printStackTrace();
		ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED,
				AppConstants.UNAUTHORIZED_ACCESS);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		String errorMessage = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			errorMessage = mapper.writeValueAsString(error);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}

		byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
		response.getOutputStream().write(bytes);

	}

}
