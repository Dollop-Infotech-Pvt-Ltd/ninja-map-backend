package com.ninjamap.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ninjamap.app.exception.BadRequestException;
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

	@Override
	public void createSession(Object account, String accessToken, String refreshToken, String userAgent,
			String ipAddress) {
		String deviceType = deviceMetadataUtil.getDeviceType(userAgent);
		CompletableFuture<String> locationFuture = deviceMetadataUtil.getLocationFromIp(ipAddress);

		String accountId;
		String accountType;
		String roleName;

		if (account instanceof User user) {
			accountId = user.getUserId();
			accountType = "USER";
			roleName = user.getRole().getRoleName();
		} else if (account instanceof Admin admin) {
			accountId = admin.getAdminId();
			accountType = "ADMIN";
			roleName = admin.getRole().getRoleName();
		} else {
			throw new BadRequestException("Unknown account type for session creation");
		}

		Session session = Session.builder().accountId(accountId).accountType(accountType).roleName(roleName)
				.accessToken(accessToken).refreshToken(refreshToken).deviceType(deviceType).ipAddress(ipAddress)
				.location(locationFuture.join()).loginTime(LocalDateTime.now()).lastActiveTime(LocalDateTime.now())
				.userAgent(userAgent).build();

		sessionRepository.save(session);
	}

	@Override
	public List<SessionResponse> getActiveSessions() {
		String token = utils.getToken(httpServletRequest);
		String email = utils.extractEmail(token);

		Object account;
		try {
			account = userService.getUserByEmailAndIsActive(email, true);
		} catch (ResourceNotFoundException e) {
			account = adminService.getAdminByEmailAndIsActive(email, true);
		}

		String accountId = account instanceof User user ? user.getUserId() : ((Admin) account).getAdminId();
		List<Session> sessions = sessionRepository.findAllByAccountId(accountId);

		return sessions.stream().map(session -> SessionResponse.builder().id(session.getId())
				.accountId(session.getAccountId()).roleName(session.getRoleName()).deviceType(session.getDeviceType())
				.ipAddress(session.getIpAddress()).location(session.getLocation()).loginTime(session.getLoginTime())
				.lastActiveTime(session.getLastActiveTime()).userAgent(session.getUserAgent()).build()).toList();
	}

	@Transactional
	@Override
	public ResponseEntity<ApiResponse> logout() {
		String token = utils.getToken(httpServletRequest);

		if (!utils.isAccessToken(token)) {
			throw new UnauthorizedException(AppConstants.INVALID_TOKEN_TYPE);
		}
		System.err.println(token);

		Session session = sessionRepository.findByAccessToken(token)
				.orElseThrow(() -> new UnauthorizedException(AppConstants.SESSION_ALREADY_LOGGED_OUT));
		System.err.println(session);
		sessionRepository.delete(session);
		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.LOGOUT));
	}

	@Transactional
	@Override
	public ResponseEntity<ApiResponse> logoutBySessionControl(String currentSessionId, Boolean keepOnlyCurrentSession) {
		Session currentSession = sessionRepository.findById(currentSessionId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SESSION_NOT_FOUND));

		String accountId = currentSession.getAccountId();

		if (keepOnlyCurrentSession) {
			sessionRepository.deleteByAccountIdAndIdNot(accountId, currentSessionId);
		} else {
			sessionRepository.delete(currentSession);
		}

		return ResponseEntity
				.ok(AppUtils.buildSuccessResponse(keepOnlyCurrentSession ? AppConstants.ALL_SESSION_LOGGED_OUT
						: AppConstants.CURRENT_SESSION_LOGGED_OUT));
	}

	@Override
	public void updateAccessTokenForRefreshToken(Object account, String oldRefreshToken, String newAccessToken) {
		Session session = sessionRepository.findByRefreshToken(oldRefreshToken)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.REFRESH_TOKEN_NOT_FOUND));

		String accountId = account instanceof User user ? user.getUserId() : ((Admin) account).getAdminId();

		if (!session.getAccountId().equals(accountId)) {
			throw new UnauthorizedException(AppConstants.TOKEN_NOT_BELONG_TO_USER);
		}

		session.setAccessToken(newAccessToken);
		sessionRepository.save(session);
	}

	@Override
	public ResponseEntity<ApiResponse> getTokenFromId(String sessionId) {
		Session session = sessionRepository.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.SESSION_NOT_FOUND));

		Map<String, String> tokenData = Map.of(AppConstants.ACCESS_TOKEN, session.getAccessToken()
//				,AppConstants.REFRESH_TOKEN, session.getRefreshToken()
		);

		return ResponseEntity.ok(AppUtils.buildSuccessResponse(AppConstants.ACCESS_TOKEN_FETCH, tokenData));
	}
}
