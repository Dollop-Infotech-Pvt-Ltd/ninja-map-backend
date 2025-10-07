package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TrimValidator
public class OtpRequest {

	@NotBlank(message = ValidationConstants.OTP_REQUIRED)
	@Pattern(regexp = "^[0-9]{6}$", message = ValidationConstants.OTP_LENGTH_VALIDATION)
	private String otp;

}
