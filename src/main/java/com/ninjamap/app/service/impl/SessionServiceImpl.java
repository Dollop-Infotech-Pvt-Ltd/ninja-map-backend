package com.ninjamap.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjamap.app.exception.ResourceNotFoundException;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.model.Admin;
import com.ninjamap.app.model.Session;
import com.ninjamap.app.model.User;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.SessionResponse;
import com.ninjamap.app.repository.ISessionRepository;
import com.ninjamap.app.service.IAdminService;
import com.ninjamap.app.service.ISessionService;
import com.ninjamap.app.service.IUserService;
import com.ninjamap.app.utils.AppUtils;
import com.ninjamap.app.utils.DeviceMetadataUtil;
import com.ninjamap.app.utils.JwtUtils;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements ISessionService {

	private final ISessionRepository sessionRepository;
	private final HttpServletRequest httpServletRequest;
	private final DeviceMetadataUtil deviceMetadataUtil;
	private final IUserService userService;
	private final IAdminService adminService;
	private final JwtUtils utils;

	// ------------------ CREATE SESSION ------------------

	@Override
	public void createSession(Object account, String accessToken, String refreshToken, String userAgent,
			String ipAddress) {
		String deviceType = deviceMetadataUtil.getDeviceType(userAgent);
		String location = deviceMetadataUtil.getLocationFromIp(ipAddress).join();

		Session session;
		if (account instanceof User user) {
			session = Session.builder().user(user).accessToken(accessToken).refreshToken(refreshToken)
					.deviceType(deviceType).ipAddress(ipAddress).location(location).loginTime(LocalDateTime.now())
					.lastActiveTime(LocalDateTime.now()).userAgent(userAgent).build();
		} else if (account instanceof Admin admin) {
			session = Session.builder().admin(admin).accessToken(accessToken).refreshToken(refreshToken)
					.deviceType(deviceType).ipAddress(ipAddress).location(location).loginTime(LocalDateTime.now())
					.lastActiveTime(LocalDateTime.now()).userAgent(userAgent).build();
		} else {
			throw new IllegalArgumentException("Unknown account type for session creation");
		}

		sessionRepository.save(session);
	}

	// ------------------ GET ACTIVE SESSIONS ------------------
	@Override
	public List<SessionResponse> getActiveSessions() {
		String token = utils.getToken(httpServletRequest);
		String email = utils.extractEmail(token);
		String role = utils.extractRole(token);

		List<Session> sessions;

		if ("USER".equals(role)) {
			User user = userService.getUserByEmailAndIsActive(email, true);
			sessions = sessionRepository.findAllByUserOrderByLoginTimeDesc(user);
		} else {
			Admin admin = adminService.getAdminByEmailAndIsActive(email, true);
			sessions = sessionRepository.findAllByAdminOrderByLoginTimeDesc(admin);
		}

		return sessions.stream()
				.map(session -> SessionResponse.builder().id(session.getId())
						.userId(session.getUser() != null ? session.getUser().getUserId() : null)
						.adminId(session.getAdmin() != null ? session.getAdmin().getAdminId() : null)
						.deviceType(session.getDeviceType()).ipAddress(session.getIpAddress())
						.location(session.getLocation()).loginTime(session.getLoginTime())
						.lastActiveTime(session.getLastActiveTime()).userAgent(session.getUserAgent()).build())
				.toList();
	}

	// ------------------ LOGOUT ------------------

	@Transactional
	@Override
	public ResponseEntity<ApiResponse> logout() {
		String token = utils.getToken(httpServletRequest);

		if (!utils.isAccessToken(token)) {
			throw new UnauthorizedException(AppConstants.INVALID_TOKEN_TYPE);
		}

		Session session = sessionRepository.findByAccessToken(token)
				.orElseThrow(() -> new UnauthorizedException(AppConstants.SESSION_ALREADY_LOGGED_OUT));

		sessionRepository.delete(session);
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.LOGOUT));
	}

	@Transactional
	@Override
	public ResponseEntity<ApiResponse> logoutBySessionControl(String currentSessionId, Boolean keepOnlyCurrentSession) {
		Session currentSession = sessionRepository.findById(currentSessionId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SESSION_NOT_FOUND));

		if (keepOnlyCurrentSession) {
			if (currentSession.getUser() != null) {
				sessionRepository.deleteByUserAndIdNot(currentSession.getUser(), currentSessionId);
			} else if (currentSession.getAdmin() != null) {
				sessionRepository.deleteByAdminAndIdNot(currentSession.getAdmin(), currentSessionId);
			}
		} else {
			sessionRepository.delete(currentSession);
		}

		return ResponseEntity
				.ok(AppUtils.buildSuccessResponse(keepOnlyCurrentSession ? AppConstants.ALL_SESSION_LOGGED_OUT
						: AppConstants.CURRENT_SESSION_LOGGED_OUT));
	}

	// ------------------ REFRESH TOKEN UPDATE ------------------

	@Override
	public void updateAccessTokenForRefreshToken(Object account, String oldRefreshToken, String newAccessToken) {
		Session session = sessionRepository.findByRefreshToken(oldRefreshToken)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.REFRESH_TOKEN_NOT_FOUND));

		if (account instanceof User user) {
			if (session.getUser() == null || !session.getUser().getUserId().equals(user.getUserId())) {
				throw new UnauthorizedException(AppConstants.TOKEN_NOT_BELONG_TO_USER);
			}
		} else if (account instanceof Admin admin) {
			if (session.getAdmin() == null || !session.getAdmin().getAdminId().equals(admin.getAdminId())) {
				throw new UnauthorizedException(AppConstants.TOKEN_NOT_BELONG_TO_USER);
			}
		} else {
			throw new IllegalArgumentException("Unknown account type");
		}

		session.setAccessToken(newAccessToken);
		sessionRepository.save(session);
	}

	// ------------------ GET TOKEN FROM SESSION ------------------

	@Override
	public ResponseEntity<ApiResponse> getTokenFromId(String sessionId) {
		Session session = sessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SESSION_NOT_FOUND));

		Map<String, String> tokenData = Map.of(AppConstants.ACCESS_TOKEN, session.getAccessToken());

		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.ACCESS_TOKEN_FETCH, tokenData));
	}
}
