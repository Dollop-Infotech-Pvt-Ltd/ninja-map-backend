package com.ninjamap.app.payload.request;

import org.hibernate.validator.constraints.UUID;
import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TrimValidator
public class UpdateUserRequest {

	@NotBlank(message = ValidationConstants.ID_REQUIRED)
	@UUIDValidator(message = ValidationConstants.INVALID_UUID)
	private String id;

	@Size(max = 50, message = ValidationConstants.FIRST_NAME_LENGTH)
	private String firstName;

	@Size(max = 50, message = ValidationConstants.LAST_NAME_LENGTH)
	private String lastName;

//	@NotBlank(message = ValidationConstants.MOBILE_NUMBER_REQUIRED)
//	@Pattern(regexp = ValidationConstants.MOBILE_NUMBER_PATTERN, message = ValidationConstants.MOBILE_NUMBER_PATTERN_MESSAGE)
//	private String mobileNumber;
//
//	@NotBlank(message = ValidationConstants.EMAIL_REQUIRED)
//	@Email(regexp = ValidationConstants.EMAIL_PATTERN, message = ValidationConstants.EMAIL_PATTERN_MESSAGE)
//	private String email;

	private MultipartFile profilePicture;

	@Size(min = 10, max = 300, message = ValidationConstants.BIO_LENGTH)
	private String bio;

	private String gender;
}
