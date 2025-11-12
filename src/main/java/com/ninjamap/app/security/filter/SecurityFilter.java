package com.ninjamap.app.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.response.ErrorResponse;
import com.ninjamap.app.repository.IAdminRepository;
import com.ninjamap.app.repository.ISessionRepository;
import com.ninjamap.app.repository.IUserRepository;
import com.ninjamap.app.service.impl.AdminServiceImpl;
import com.ninjamap.app.service.impl.UserServiceImpl;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final UserServiceImpl userDetailsService;
	private final AdminServiceImpl adminDetailsService;
	private final IUserRepository userRepository;
	private final IAdminRepository adminRepository;
	private final ISessionRepository sessionRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		System.err.println("REQUEST URL ==> " + request.getRequestURI());
		System.err.println("TOKEN ==> "+authHeader);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			try {
				if (jwtUtils.extractTokenType(token) == TokenType.ACCESS_TOKEN && !jwtUtils.isTokenExpired(token)) {
					Optional<?> sessionOpt = sessionRepository.findByAccessToken(token);

					if (sessionOpt.isEmpty()) {
						respondUnauthorized(response, "Your session has expired or you have been logged out.");
						return;
					}

					String email = jwtUtils.extractEmail(token);
					String role = jwtUtils.extractRole(token);

					AccountInfo accountInfo = getAccountInfo(email, role, response);
					if (accountInfo == null)
						return;

					authenticate(accountInfo.userDetails, request);

					// Update session last active time
					sessionRepository.findByAccessToken(token).ifPresent(session -> {
						session.setLastActiveTime(LocalDateTime.now());
						sessionRepository.save(session);
					});
				}
			} catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnauthorizedException ex) {
				handleException(response, AppConstants.INVALID_TOKEN);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return "OPTIONS".equalsIgnoreCase(request.getMethod());
	}

	private void authenticate(UserDetails userDetails, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userDetails.getUsername(), null, userDetails.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
		SecurityContextHolder.clearContext();
		sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
	}

	private void sendResponse(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		ErrorResponse error = new ErrorResponse(status, HttpStatus.valueOf(status), message);
		ObjectMapper mapper = new ObjectMapper();
		byte[] bytes = mapper.writeValueAsString(error).getBytes(StandardCharsets.UTF_8);
		response.getOutputStream().write(bytes);
		response.getOutputStream().close();
	}

	private record AccountInfo(UserDetails userDetails) {
	}

	private AccountInfo getAccountInfo(String email, String role, HttpServletResponse response) throws IOException {
		if ("USER".equals(role)) {
			return checkAccount(
					userRepository.findByPersonalInfo_Email(email)
							.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND)),
					userDetailsService::loadUserByUsername, AppConstants.USER_ACCOUNT_INACTIVE,
					AppConstants.USER_ACCOUNT_DELETED, response);
		} else {
			return checkAccount(
					adminRepository.findByPersonalInfo_Email(email)
							.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ADMIN_NOT_FOUND)),
					adminDetailsService::loadUserByUsername, AppConstants.ADMIN_ACCOUNT_INACTIVE,
					AppConstants.ADMIN_ACCOUNT_DELETED, response);
		}
	}

	private <T> AccountInfo checkAccount(T account, java.util.function.Function<String, UserDetails> loadUserDetails,
			String inactiveMessage, String deletedMessage, HttpServletResponse response) throws IOException {

		boolean isActive = false;
		boolean isDeleted = false;
		String email = null;

		if (account instanceof User user) {
			isActive = Boolean.TRUE.equals(user.getIsActive());
			isDeleted = Boolean.TRUE.equals(user.getIsDeleted());
			email = user.getPersonalInfo().getEmail();
		} else if (account instanceof Admin admin) {
			isActive = Boolean.TRUE.equals(admin.getIsActive());
			isDeleted = Boolean.TRUE.equals(admin.getIsDeleted());
			email = admin.getPersonalInfo().getEmail();
		}

		if (!isActive && !isDeleted) {
			sendResponse(response, HttpServletResponse.SC_FORBIDDEN, inactiveMessage);
			return null;
		} else if (!isActive && isDeleted) {
			sendResponse(response, HttpServletResponse.SC_NOT_FOUND, deletedMessage);
			return null;
		}

		return new AccountInfo(loadUserDetails.apply(email));
	}

	public void handleException(HttpServletResponse response, String message) {
		try {
			sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
