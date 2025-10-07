package com.ninjamap.app.model;

import java.time.LocalDateTime;

import com.ninjamap.app.enums.OtpType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode(callSuper = false)
@Table(name = "otp_tab")
public class Otp {

	@Id
	private String email;

	private String otp;

	private LocalDateTime expirationTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "otp_type")
	private OtpType otpType;
}
