package com.ninjamap.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Session extends AuditData {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(name = "account_id", nullable = false)
	private String accountId; // User or Admin ID

	@Column(name = "account_type", nullable = false)
	private String accountType; // "USER" or "ADMIN"

	@Column(name = "role_name", nullable = false)
	private String roleName; // Dynamic role like SUPER_ADMIN, SUB_ADMIN, CUSTOMER, etc.

	@Column(length = 512, nullable = false, unique = true)
	private String accessToken;

	@Column(length = 512, nullable = false, unique = true)
	private String refreshToken;

	@Column(name = "device_type")
	private String deviceType;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "location")
	private String location;

	@Column(name = "login_time")
	private LocalDateTime loginTime;

	@Column(name = "last_active_time")
	private LocalDateTime lastActiveTime;

	@Column(name = "user_agent", length = 512)
	private String userAgent;

}
