package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for returning session information to clients without exposing
 * sensitive data like tokens.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponse {

	private String id;
	private String accountId;
	private String roleName;
	private String deviceType;
	private String ipAddress;
	private String location;
	private LocalDateTime loginTime;
	private LocalDateTime lastActiveTime;
	private String userAgent;
}
