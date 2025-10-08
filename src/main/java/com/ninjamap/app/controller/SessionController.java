package com.ninjamap.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.payload.response.SessionResponse;
import com.ninjamap.app.service.ISessionService;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sessions")
@Validated
public class SessionController {
	private final ISessionService sessionService;

	/**
	 * Get all active sessions for the current user/account.
	 */
	@PreAuthorize("hasAuthority('SESSION_MANAGEMENT.VIEW_SESSION')")
	@GetMapping("/active")
	public ResponseEntity<List<SessionResponse>> getActiveSessions() {
		return ResponseEntity.ok(sessionService.getActiveSessions());
	}

	@PreAuthorize("hasAuthority('SESSION_MANAGEMENT.VIEW_SESSION')")
	@GetMapping("/get-access-token")
	public ResponseEntity<ApiResponse> getTokenBySessionId(
			@RequestParam(name = AppConstants.SESSION_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String sessionId) {
		return sessionService.getTokenFromId(sessionId);
	}

	/**
	 * Logout from all sessions.
	 */
	@PreAuthorize("hasAuthority('SESSION_MANAGEMENT.LOGOUT_SESSION')")
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse> logout() {
		return sessionService.logout();
	}

	/**
	 * Logout specific sessions based on control options.
	 * 
	 * @param currentSessionId       The ID of the current session
	 * @param keepOnlyCurrentSession Whether to keep only the current session active
	 */
	@PreAuthorize("hasAuthority('SESSION_MANAGEMENT.LOGOUT_SESSION')")
	@PostMapping("/logout/control")
	public ResponseEntity<ApiResponse> logoutBySessionControl(
			@RequestParam(name = AppConstants.CURRENT_SESSION_ID) @UUIDValidator(message = ValidationConstants.INVALID_UUID) String currentSessionId,
			@RequestParam(name = AppConstants.KEEP_ONLY_CURRENT_SESSION) Boolean keepOnlyCurrentSession) {

		return sessionService.logoutBySessionControl(currentSessionId, keepOnlyCurrentSession);
	}
}