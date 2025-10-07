package com.ninjamap.app.security;

import java.io.IOException;
import java.net.URLEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

	private final String redirectBaseUrl = "http://localhost:4200";

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// redirect with error message
		String redirectUri = redirectBaseUrl + "/oauth-error?message="
				+ URLEncoder.encode(exception.getMessage(), "UTF-8");
		response.sendRedirect(redirectUri);
	}
}
