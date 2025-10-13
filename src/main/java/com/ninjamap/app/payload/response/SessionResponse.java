package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@JsonInclude(Include.NON_NULL)
public class SessionResponse {

	private String id;
	private String userId;
	private String adminId;
	private String deviceType;
	private String ipAddress;
	private String location;
	private LocalDateTime loginTime;
	private LocalDateTime lastActiveTime;
	private String userAgent;
}
