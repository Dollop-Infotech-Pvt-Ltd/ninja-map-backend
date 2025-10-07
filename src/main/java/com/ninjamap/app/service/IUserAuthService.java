package com.ninjamap.app.service;

import com.ninjamap.app.payload.request.RegisterRequest;
import com.ninjamap.app.payload.response.ApiResponse;

public interface IUserAuthService extends IAuthenticationService {
	ApiResponse register(RegisterRequest request);

}
