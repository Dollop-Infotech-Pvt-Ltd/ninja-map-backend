package com.ninjamap.app.service;

import com.ninjamap.app.enums.OtpType;

public interface IOtpService {

	String generateOtp(String email, OtpType otpType);

	boolean validateOtp(String email, String otp, OtpType otpType);

}
