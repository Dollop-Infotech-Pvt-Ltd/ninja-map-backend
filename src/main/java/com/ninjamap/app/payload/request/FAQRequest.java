package com.ninjamap.app.payload.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FAQRequest {

	@NotBlank(message = "Category is required")
	@Size(max = 100, message = "Category cannot exceed 100 characters")
	private String category;

//	@Size(max = 255, message = "Category image URL cannot exceed 255 characters")
	@NotNull(message = ValidationConstants.IMAGE_REQUIRED)
	private MultipartFile categoryImageUrl;

	@NotEmpty(message = "Questions list cannot be empty")
	private List<@Valid QuestionAnswerDTO> questions; // Ensure each question/answer is validated
}
