package com.ninjamap.app.security.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.User;
import com.ninjamap.app.repository.IAdminRepository;
import com.ninjamap.app.repository.ISessionRepository;
import com.ninjamap.app.repository.IUserRepository;
import com.ninjamap.app.service.impl.AdminServiceImpl;
import com.ninjamap.app.service.impl.UserServiceImpl;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

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

	/**
	 * Main filter method that intercepts every request (except OPTIONS) and
	 * validates JWT tokens for User or Admin.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		System.err.println("REQUEST URI ===> " + request.getRequestURI());

		// Only process Bearer tokens
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			System.err.println("Token found: " + token);

			try {
				// Validate token type and expiration
				if (jwtUtils.extractTokenType(token).equals(TokenType.ACCESS_TOKEN)
						&& !jwtUtils.isTokenExpired(token)) {

					// Check if token exists in session repository
					Optional<?> sessionOpt = sessionRepository.findByAccessToken(token);
					if (sessionOpt.isEmpty()) {
						respondUnauthorized(response, "Your session has expired or you have been logged out.");
						return;
					}

					String email = jwtUtils.extractEmail(token);
					String role = jwtUtils.extractRole(token);

					// Get UserDetails for either User or Admin
					AccountInfo accountInfo = getAccountInfoByRole(email, role, response);
					if (accountInfo == null)
						return; // Response already sent

					UserDetails userDetails = accountInfo.userDetails;

					// Set Spring Security context
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails.getUsername(), null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);

					// Update session last active time
					sessionRepository.findByAccessToken(token).ifPresent(session -> {
						session.setLastActiveTime(LocalDateTime.now());
						sessionRepository.save(session);
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				SecurityContextHolder.clearContext();
			}
		}

		// Proceed to next filter in chain
		chain.doFilter(request, response);

	}

	/**
	 * Skip filtering for OPTIONS requests (CORS preflight)
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return "OPTIONS".equalsIgnoreCase(request.getMethod());
	}

	/**
	 * Sends 401 Unauthorized response and clears security context
	 */
	private void respondUnauthorized(HttpServletResponse response, String message) throws IOException {
		SecurityContextHolder.clearContext();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(message);
	}

	/**
	 * Helper record to wrap UserDetails (immutable)
	 */
	private record AccountInfo(UserDetails userDetails) {
	}

	/**
	 * Retrieve UserDetails for either User or Admin based on role. Also handles
	 * inactive or deleted accounts with appropriate HTTP response.
	 */
	private AccountInfo getAccountInfoByRole(String email, String role, HttpServletResponse response)
			throws IOException {

		if ("USER".equals(role)) {
			// Fetch user by email
			User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));

			// Check account status
			if (!Boolean.TRUE.equals(user.getIsActive()) && !Boolean.TRUE.equals(user.getIsDeleted())) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().write(AppConstants.USER_ACCOUNT_INACTIVE);
				return null;
			} else if (!Boolean.TRUE.equals(user.getIsActive()) && Boolean.TRUE.equals(user.getIsDeleted())) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().write(AppConstants.USER_ACCOUNT_DELETED);
				return null;
			}

			// Load UserDetails
			return new AccountInfo(userDetailsService.loadUserByUsername(email));
		} else {
			// Fetch admin by email
			Admin admin = adminRepository.findByEmail(email)
					.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ADMIN_NOT_FOUND));

			// Check account status
			if (!Boolean.TRUE.equals(admin.getIsActive()) && !Boolean.TRUE.equals(admin.getIsDeleted())) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getWriter().write(AppConstants.ADMIN_ACCOUNT_INACTIVE);
				return null;
			} else if (!Boolean.TRUE.equals(admin.getIsActive()) && Boolean.TRUE.equals(admin.getIsDeleted())) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().write(AppConstants.ADMIN_ACCOUNT_DELETED);
				return null;
			}

			// Load UserDetails
			return new AccountInfo(adminDetailsService.loadUserByUsername(email));
		}
	}
}
