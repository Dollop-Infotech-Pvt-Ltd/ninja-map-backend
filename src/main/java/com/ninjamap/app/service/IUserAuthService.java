package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.AppRegisterRequest;
import com.ninjamap.app.payload.request.ChangePasswordRequest;
import com.ninjamap.app.payload.request.MobileLoginRequest;
import com.ninjamap.app.payload.request.RegisterRequest;
import com.ninjamap.app.payload.response.ApiResponse;

import jakarta.validation.Valid;

public interface IUserAuthService extends IAuthenticationService {
	ApiResponse register(RegisterRequest request);

	ApiResponse registerFromApp(AppRegisterRequest request);

	ApiResponse loginWithMobile(MobileLoginRequest request);


}
