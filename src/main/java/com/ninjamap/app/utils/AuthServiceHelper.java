package com.ninjamap.app.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.utils.constants.AppConstants;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthServiceHelper {

	private final JwtUtils jwtUtils;
	private final DeviceMetadataUtil deviceMetadataUtil;
	private final HttpServletRequest httpRequest;

	public AuthServiceHelper(JwtUtils jwtUtils, DeviceMetadataUtil deviceMetadataUtil, HttpServletRequest httpRequest) {
		this.jwtUtils = jwtUtils;
		this.deviceMetadataUtil = deviceMetadataUtil;
		this.httpRequest = httpRequest;
	}

	/**
	 * Determine if an AuthToken is required based on OTP type.
	 */
	public boolean isAuthTokenRequired(OtpType otpType) {
		return resolveTokenType(otpType) == TokenType.AUTH_TOKEN;
	}

	/**
	 * Resolve the token type based on OTP type.
	 */
	public TokenType resolveTokenType(OtpType otpType) {
		return otpType == OtpType.FORGET_PASSWORD ? TokenType.AUTH_TOKEN : TokenType.ACCESS_TOKEN;
	}

	/**
	 * Generate access and refresh tokens.
	 */
	public Map<String, Object> generateAccessAndRefreshTokens(String email, String role, OtpType otpType) {
		String accessToken = jwtUtils.generateToken(email, role, resolveTokenType(otpType), otpType, true);
		String refreshToken = jwtUtils.generateRefreshToken(email, role);

		Map<String, Object> tokens = new HashMap<>();
		tokens.put(AppConstants.ACCESS_TOKEN, accessToken);
		tokens.put(AppConstants.REFRESH_TOKEN, refreshToken);
		return tokens;
	}
}
