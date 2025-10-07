package com.ninjamap.app.security.filter;

import java.io.IOException;

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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		System.err.println("REQUEST URI ===> " + request.getRequestURI());
//		System.err.println("..authheader in filter " + request.getHeader("X-XSRF-TOKEN"));

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			System.err.println(".........token " + token);

			try {
				if (jwtUtils.extractTokenType(token).equals(TokenType.ACCESS_TOKEN)
						&& !jwtUtils.isTokenExpired(token)) {

					String email = jwtUtils.extractEmail(token);
					String role = jwtUtils.extractRole(token);
					UserDetails userDetails;
					boolean isActive;
					boolean isDeleted;
					if (role != null && "USER".equals(role)) {
						User user = userRepository.findByEmail(email)
								.orElseThrow(() -> new ResourceNotFoundException(AppConstants.USER_NOT_FOUND));
						isActive = Boolean.TRUE.equals(user.getIsActive());
						isDeleted = Boolean.TRUE.equals(user.getIsDeleted());
						userDetails = userDetailsService.loadUserByUsername(email);
						System.err.println("USER DETAILS ==> " + userDetails);
					} else {
						Admin admin = adminRepository.findByEmail(email)
								.orElseThrow(() -> new ResourceNotFoundException(AppConstants.ADMIN_NOT_FOUND));
						isActive = Boolean.TRUE.equals(admin.getIsActive());
						isDeleted = Boolean.TRUE.equals(admin.getIsDeleted());
						userDetails = adminDetailsService.loadUserByUsername(email);
					}

					// Logout if inactive or deleted
					if (!isActive || isDeleted) {
						sessionRepository.findByAccessToken(token).ifPresent(sessionRepository::delete);
						SecurityContextHolder.clearContext();
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.getWriter().write("User inactive or deleted. Logged out.");
						return;
					}

					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails.getUsername(), null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);

					// --- SESSION MANAGEMENT ---
					sessionRepository.findByAccessToken(token).ifPresent(session -> {
						session.setLastActiveTime(java.time.LocalDateTime.now());
						sessionRepository.save(session);
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				SecurityContextHolder.clearContext();
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return "OPTIONS".equalsIgnoreCase(request.getMethod());
	}
}