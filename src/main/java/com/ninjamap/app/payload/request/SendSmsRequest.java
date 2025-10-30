package com.ninjamap.app.payload.request;

import com.ninjamap.app.enums.EmailTemplateType;
import com.ninjamap.app.enums.OtpType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendSmsRequest {
	private String phoneNumber;
	private String message;
//	private String password;
	private String otp;
	private OtpType otpType;
	private EmailTemplateType templateType;
	private String dataTime;
}
