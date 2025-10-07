package com.ninjamap.app.service;

import java.util.List;

import com.ninjamap.app.model.Session;
import com.ninjamap.app.payload.response.ApiResponse;

public interface ISessionService {
	void createSession(Object account, String accessToken, String refreshToken, String userAgent, String ipAddress);

	List<Session> getActiveSessions();

	ApiResponse logout();

	ApiResponse logoutBySessionControl(String currentSessionId, boolean keepOnlyCurrentSession);

	void updateAccessTokenForRefreshToken(Object account, String oldRefreshToken, String newAccessToken);

}
