package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CommentRequest {

	@NotBlank(message = "Name is required")
	private String name;

	@NotBlank(message = ValidationConstants.EMAIL_REQUIRED)
	@Email(regexp = ValidationConstants.EMAIL_PATTERN, message = ValidationConstants.EMAIL_PATTERN_MESSAGE)
	private String email;

	@NotBlank(message = "Content is required")
	@Size(min = 10, message = "Content must be at least 10 characters long")
	private String content;

}
