package com.ninjamap.app.payload.request;

import com.ninjamap.app.enums.EmailTemplateType;
import com.ninjamap.app.enums.OtpType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailRequest {
	private String to; // recipient email
	private String username;
	private String otp;
	private OtpType otpType;
	private EmailTemplateType templateType;
	private String dataTime;
}
