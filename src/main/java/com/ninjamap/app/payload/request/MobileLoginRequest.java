package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TrimValidator
public class MobileLoginRequest {
	@NotBlank(message = ValidationConstants.MOBILE_NUMBER_REQUIRED)
	@Pattern(regexp = ValidationConstants.MOBILE_NUMBER_PATTERN, message = ValidationConstants.MOBILE_NUMBER_PATTERN_MESSAGE)
	private String mobileNumber;
}
