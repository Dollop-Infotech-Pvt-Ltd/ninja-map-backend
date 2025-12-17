package com.ninjamap.app.payload.request;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TrimValidator
public class RegisterRequest {

	@NotBlank(message = ValidationConstants.EMAIL_REQUIRED)
	@Email(regexp = ValidationConstants.EMAIL_PATTERN, message = ValidationConstants.EMAIL_PATTERN_MESSAGE)
	private String email;

	@Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, max = ValidationConstants.PASSWORD_MAX_LENGTH, message = ValidationConstants.PASSWORD_SIZE_MESSAGE)
	@NotBlank(message = ValidationConstants.PASSWORD_REQUIRED)
	@Pattern(regexp = ValidationConstants.PASSWORD_PATTERN, message = ValidationConstants.PASSWORD_PATTERN_MESSAGE)
	private String password;

	@NotBlank(message = ValidationConstants.FIRST_NAME_REQUIRED)
	@Size(max = 50, message = ValidationConstants.FIRST_NAME_LENGTH)
	private String firstName;

	@NotBlank(message = ValidationConstants.LAST_NAME_REQUIRED)
	@Size(max = 50, message = ValidationConstants.LAST_NAME_LENGTH)
	private String lastName;

	@NotBlank(message = ValidationConstants.MOBILE_NUMBER_REQUIRED)
	@Pattern(regexp = ValidationConstants.MOBILE_NUMBER_PATTERN, message = ValidationConstants.MOBILE_NUMBER_PATTERN_MESSAGE)
	private String mobileNumber;
}