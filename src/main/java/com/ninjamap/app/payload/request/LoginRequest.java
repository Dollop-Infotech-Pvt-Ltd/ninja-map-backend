package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TrimValidator
public class LoginRequest {

	@NotBlank(message = ValidationConstants.USERNAME_REQUIRED)
	private String username;

	@NotBlank(message = ValidationConstants.PASSWORD_REQUIRED)
	private String password;

}