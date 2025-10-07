package com.ninjamap.app.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ninjamap.app.enums.OtpType;
import com.ninjamap.app.enums.TokenType;
import com.ninjamap.app.exception.UnauthorizedException;
import com.ninjamap.app.utils.constants.AppConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.issuer}")
	private String issuer;

	@Value("${jwt.auth.expiry}")
	private long authTokenExpiry;

	@Value("${jwt.access.expiry}")
	private long accessTokenExpiry;

	@Value("${jwt.refresh.expiry}")
	private long refreshTokenExpiry;

//	@Autowired
//	private HttpServletRequest request;

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String email, String role, TokenType tokenType, OtpType otpType,
			Boolean isOtpVerified) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(AppConstants.ROLE, role);
		claims.put(AppConstants.TOKEN_TYPE, tokenType.name());
		claims.put(AppConstants.OTP_TYPE, otpType != null ? otpType.name() : null);
		claims.put(AppConstants.IS_OTP_VERIFIED, isOtpVerified);
		return buildToken(claims, email, tokenType);
	}

	private String buildToken(Map<String, Object> claims, String subject, TokenType tokenType) {
		long expiry;

		switch (tokenType) {
		case AUTH_TOKEN:
			expiry = authTokenExpiry;
			break;
		case ACCESS_TOKEN:
			expiry = accessTokenExpiry;
			break;
		default:
			expiry = authTokenExpiry;
			break;
		}

		return Jwts.builder().claims(claims).subject(subject).issuedAt(new Date()).issuer(issuer)
				.expiration(new Date(System.currentTimeMillis() + expiry)).signWith(getSecretKey()).compact();
	}

	public String generateRefreshToken(String email, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(AppConstants.ROLE, role);
		claims.put(AppConstants.TOKEN_TYPE, TokenType.REFRESH_TOKEN.name());

		return Jwts.builder().claims(claims).subject(email).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + refreshTokenExpiry)).signWith(getSecretKey())
				.compact();
	}

//	private Claims extractClaims(String token) {
//		return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody();
//	}

	private Claims extractClaims(String token) {
		return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
	}

	public String extractEmail(String token) {
		return extractClaims(token).getSubject();
	}

	public TokenType extractTokenType(String token) {
		return TokenType.valueOf((String) extractClaims(token).get(AppConstants.TOKEN_TYPE));
	}

	public Boolean extractIsOtpVerified(String token) {
		return (Boolean) extractClaims(token).get(AppConstants.IS_OTP_VERIFIED);
	}

	public OtpType extractOtpType(String token) {
		String otpType = (String) extractClaims(token).get(AppConstants.OTP_TYPE);
		return otpType != null ? OtpType.valueOf(otpType) : null;
	}

	public String extractRole(String token) {
		return (String) extractClaims(token).get(AppConstants.ROLE);
	}

	public boolean isTokenExpired(String token) {
		return extractClaims(token).getExpiration().before(new Date());
	}

	public boolean isAccessToken(String token) {
		return extractTokenType(token) == TokenType.ACCESS_TOKEN;
	}

	public boolean isRefreshToken(String token) {
		return extractTokenType(token) == TokenType.REFRESH_TOKEN;
	}

	public boolean validateRefreshToken(String refreshToken) {
		return isRefreshToken(refreshToken) && !isTokenExpired(refreshToken);
	}

	public String getToken(HttpServletRequest request) {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}

	public String extractTokenFromHeader() {
		// Get current request
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7); // Remove "Bearer " prefix
		}

		throw new UnauthorizedException(AppConstants.INVALID_TOKEN);
	}

//	public void generateCsrf() {
//		request.getSession(true);
//	}
}
