package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TrimValidator
public class ForgetPasswordRequest {
	@NotBlank(message = ValidationConstants.USERNAME_REQUIRED)
	private String username;
}
