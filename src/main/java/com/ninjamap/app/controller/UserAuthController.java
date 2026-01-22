package com.ninjamap.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ninjamap.app.payload.request.AppRegisterRequest;
import com.ninjamap.app.payload.request.ChangePasswordRequest;
import com.ninjamap.app.payload.request.ForgetPasswordRequest;
import com.ninjamap.app.payload.request.LoginRequest;
import com.ninjamap.app.payload.request.MobileLoginRequest;
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

	// ========================= GET CSRF TOKEN =========================
	@GetMapping("/csrf")
	public ResponseEntity<CsrfToken> getCsrfToken(HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		return ResponseEntity.ok(token);
	}

	// ========================= LOGIN =========================
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userAuthService.login(loginRequest));
	}

	// ========================= REGISTER =========================
	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(userAuthService.register(request));
	}

	// ========================= VERIFY OTP =========================
	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpRequest request) {
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
	

	
	@PostMapping("/app/login")
	public ResponseEntity<?> loginWithMobile(@Valid @RequestBody MobileLoginRequest loginRequest) {
		return ResponseEntity.ok(userAuthService.loginWithMobile(loginRequest));
	}
	
	@PostMapping("/app/register")
	public ResponseEntity<?> appRegister(@Valid AppRegisterRequest request) {
	    return ResponseEntity.ok(userAuthService.registerFromApp(request));
	}

}
