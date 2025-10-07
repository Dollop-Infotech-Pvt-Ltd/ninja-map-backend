package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.ForgetPasswordRequest;
import com.ninjamap.app.payload.request.LoginRequest;
import com.ninjamap.app.payload.request.OtpRequest;
import com.ninjamap.app.payload.request.ResetPasswordRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IAuthenticationService {

//	ApiResponse register(RegisterRequest request);

	ApiResponse login(LoginRequest request);

	ApiResponse verifyOtp(OtpRequest request);

	ApiResponse resendOtp();

	ApiResponse forgotPassword(ForgetPasswordRequest request);

	ApiResponse resetPassword(ResetPasswordRequest request);

	ApiResponse refreshToken();

//	void csrfTokenGeneration();
}
