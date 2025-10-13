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

    // ========================= LOGIN =========================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse response = adminAuthService.login(loginRequest);
        return ResponseEntity.status(response.getHttp()).body(response);
    }

    // ========================= VERIFY OTP =========================
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody OtpRequest request) {
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
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        ApiResponse response = adminAuthService.forgotPassword(request);
        return ResponseEntity.status(response.getHttp()).body(response);
    }

    // ========================= RESET PASSWORD =========================
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ApiResponse response = adminAuthService.resetPassword(request);
        return ResponseEntity.status(response.getHttp()).body(response);
    }

    // ========================= REFRESH TOKEN =========================
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken() {
        ApiResponse response = adminAuthService.refreshToken();
        return ResponseEntity.status(response.getHttp()).body(response);
    }
}
