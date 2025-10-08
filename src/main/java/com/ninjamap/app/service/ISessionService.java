package com.ninjamap.app.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.SessionResponse;

public interface ISessionService {
	void createSession(Object account, String accessToken, String refreshToken, String userAgent, String ipAddress);

	List<SessionResponse> getActiveSessions();

	ResponseEntity<ApiResponse> logout();

	ResponseEntity<ApiResponse> logoutBySessionControl(String currentSessionId, Boolean keepOnlyCurrentSession);

	void updateAccessTokenForRefreshToken(Object account, String oldRefreshToken, String newAccessToken);

	ResponseEntity<ApiResponse> getTokenFromId(String sessionId);

}
