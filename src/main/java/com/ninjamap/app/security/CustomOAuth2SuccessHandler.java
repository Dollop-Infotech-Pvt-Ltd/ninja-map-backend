package com.ninjamap.app.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.PersonalInfo;
import com.ninjamap.app.model.Roles;
import com.ninjamap.app.model.Session;
import com.ninjamap.app.model.User;
import com.ninjamap.app.repository.IAdminRepository;
import com.ninjamap.app.repository.IRolesRepository;
import com.ninjamap.app.repository.ISessionRepository;
import com.ninjamap.app.repository.IUserRepository;
import com.ninjamap.app.service.ISessionService;
import com.ninjamap.app.utils.DeviceMetadataUtil;
import com.ninjamap.app.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final String redirectBaseUrl = "http://localhost:4200";

	private final JwtUtils jwtUtils;
	private final ISessionService sessionService;
	private final DeviceMetadataUtil deviceMetadataUtil;

	private final IUserRepository userRepository;
	private final IAdminRepository adminRepository;
	private final IRolesRepository rolesRepository;
	private final ISessionRepository sessionRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws java.io.IOException {

		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		OAuth2User oauthUser = token.getPrincipal();
		String provider = token.getAuthorizedClientRegistrationId(); // google or facebook

		// Provider-specific attribute handling
		String email = oauthUser.getAttribute("email");
		String firstName = null;
		String lastName = null;

		if ("google".equalsIgnoreCase(provider)) {
			firstName = oauthUser.getAttribute("given_name");
			lastName = oauthUser.getAttribute("family_name");
		} 
//		else if ("facebook".equalsIgnoreCase(provider)) {
//			firstName = oauthUser.getAttribute("first_name");
//			lastName = oauthUser.getAttribute("last_name");
//
//			// Fallback if email not available (e.g., private FB accounts)
//			if (email == null) {
//				email = provider + "_" + oauthUser.getAttribute("id") + "@facebook.com";
//			}
//		}

		String userAgent = request.getHeader("User-Agent");
		String ip = deviceMetadataUtil.getClientIp(request);

		Object entity;
		String role;

		// Find existing user or admin
		Optional<User> userOpt = userRepository.findByEmailAndOptionalIsActive(email, null);
		Optional<Admin> adminOpt = adminRepository.findByEmailAndOptionalIsActive(email, null);

		if (userOpt.isPresent()) {
			User dbUser = userOpt.get();
			if (!dbUser.getIsActive()) {
				throw new ResourceNotFoundException("User account not active, cannot login.");
			}
			entity = dbUser;
			role = dbUser.getRole().getRoleName();
		} else if (adminOpt.isPresent()) {
			Admin dbAdmin = adminOpt.get();
			if (!dbAdmin.getIsActive()) {
				throw new ResourceNotFoundException("Admin account not active, cannot login.");
			}
			entity = dbAdmin;
			role = dbAdmin.getRole().getRoleName();
		} else {
			// Auto-register new user
			Roles userRole = rolesRepository.findByRoleName("USER")
					.orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));

			User newUser = User.builder()
					.personalInfo(
						PersonalInfo.builder()
							.email(email)
							.firstName(firstName != null ? firstName : "Unknown")
							.lastName(lastName != null ? lastName : "Unknown")
							.build()
					)
					.isActive(true)
					.isDeleted(false)
					.role(userRole)
					.build();

			entity = userRepository.save(newUser);
			System.out.println("New user registered via " + provider + ": " + email);
			role = userRole.getRoleName();
		}

		// Generate JWT tokens
		String accessToken = jwtUtils.generateToken(email, role, TokenType.ACCESS_TOKEN, null, true);
		String refreshToken = jwtUtils.generateRefreshToken(email, role);

		// Save session
		sessionService.createSession(entity, accessToken, refreshToken, userAgent, ip);

		// Get session ID for frontend
		Optional<Session> sessionOpt = sessionRepository.findByAccessToken(accessToken);
		String sessionId = sessionOpt.map(Session::getId).orElse("");

		// Redirect to frontend with session ID
		String redirectUri = redirectBaseUrl + "/oauth-success?id=" + sessionId;
		response.sendRedirect(redirectUri);
	}
}
