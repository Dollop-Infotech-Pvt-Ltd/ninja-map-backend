package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TrimValidator
public class ResetPasswordRequest {

	@Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = ValidationConstants.PASSWORD_SIZE_MESSAGE)
	@NotBlank(message = ValidationConstants.NEW_PASSWORD_REQUIRED)
	@Pattern(regexp = ValidationConstants.PASSWORD_PATTERN, message = ValidationConstants.PASSWORD_PATTERN_MESSAGE)
	private String newPassword;
}

