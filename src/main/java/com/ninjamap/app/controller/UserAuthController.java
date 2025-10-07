package com.ninjamap.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.ForgetPasswordRequest;
import com.ninjamap.app.payload.request.LoginRequest;
import com.ninjamap.app.payload.request.OtpRequest;
import com.ninjamap.app.payload.request.RegisterRequest;
import com.ninjamap.app.payload.request.ResetPasswordRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IUserAuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class UserAuthController {

	private final IUserAuthService userAuthService;

	@GetMapping("/csrf")
	public ResponseEntity<CsrfToken> getCsrfToken(HttpServletRequest request) {
		// Force generation of CSRF token
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		return ResponseEntity.ok(token);
	}

	// ========================= LOGIN =========================
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
		System.err.println("loginRequest ===> " + loginRequest);
		return ResponseEntity.ok(userAuthService.login(loginRequest));
	}

	// ========================= REGISTER =========================
	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
		System.err.println("REGISTER ===> " + request);
		return ResponseEntity.ok(userAuthService.register(request));
	}

	// OTP VERIFICATION (requires auth token in Authorization header)
	// ========================= VERIFY OTP =========================
	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpRequest request) {
		System.err.println("VERIFY_OTP");
		ApiResponse response = userAuthService.verifyOtp(request);
		return ResponseEntity.status(response.getHttp()).body(response);
	}

	// ========================= RESEND OTP =========================
	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse> resendOtp() {
		ApiResponse response = userAuthService.resendOtp();
		return ResponseEntity.status(response.getHttp()).body(response);
	}

	// ========================= FORGOT PASSWORD =========================
	@PostMapping("/forget-password")
	public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgetPasswordRequest request) {
		ApiResponse response = userAuthService.forgotPassword(request);
		return ResponseEntity.status(response.getHttp()).body(response);
	}

	// ========================= RESET PASSWORD =========================
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		ApiResponse response = userAuthService.resetPassword(request);
		return ResponseEntity.status(response.getHttp()).body(response);
	}

	// ========================= REFRESH TOKEN =========================
	@PostMapping("/refresh-token")
	public ResponseEntity<?> refresh() {
		return ResponseEntity.ok(userAuthService.refreshToken());
	}
}
