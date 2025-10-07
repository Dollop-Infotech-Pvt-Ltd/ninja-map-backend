package com.ninjamap.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.ForgetPasswordRequest;
import com.ninjamap.app.payload.request.LoginRequest;
import com.ninjamap.app.payload.request.OtpRequest;
import com.ninjamap.app.payload.request.ResetPasswordRequest;
import com.ninjamap.app.payload.response.ApiResponse;
import com.ninjamap.app.service.IAdminAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
	private final IAdminAuthService adminAuthService;

//	@GetMapping("/csrf")
//	public ResponseEntity<CsrfToken> getCsrfToken(HttpServletRequest request) {
//		// Force generation of CSRF token
//		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//		return ResponseEntity.ok(token);
//	}

	// ========================= LOGIN =========================
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(adminAuthService.login(loginRequest));
	}

	// OTP VERIFICATION (requires auth token in Authorization header)
	// ========================= VERIFY OTP =========================
	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpRequest request) {
		System.err.println("VERIFY_OTP");
		ApiResponse response = adminAuthService.verifyOtp(request);
		return ResponseEntity.status(response.getHttp()).body(response);
	}

	// ========================= RESEND OTP =========================
	@PostMapping("/resend-otp")
	public ResponseEntity<ApiResponse> resendOtp() {
		ApiResponse response = adminAuthService.resendOtp();
		return ResponseEntity.status(response.getHttp()).body(response);
	}

	// ========================= FORGOT PASSWORD =========================
	@PostMapping("/forget-password")
	public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgetPasswordRequest request) {
		ApiResponse response = adminAuthService.forgotPassword(request);
		return ResponseEntity.status(response.getHttp()).body(response);
	}

	// ========================= RESET PASSWORD =========================
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		ApiResponse response = adminAuthService.resetPassword(request);
		return ResponseEntity.status(response.getHttp()).body(response);
	}

	// ========================= REFRESH TOKEN =========================
	@PostMapping("/refresh-token")
	public ResponseEntity<?> refresh() {
		return ResponseEntity.ok(adminAuthService.refreshToken());
	}
}
