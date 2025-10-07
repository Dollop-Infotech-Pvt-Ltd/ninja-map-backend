package com.ninjamap.app.payload.request;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.utils.annotations.TrimValidator;
import com.ninjamap.app.utils.annotations.UUIDValidator;
import com.ninjamap.app.utils.constants.AppConstants;
import com.ninjamap.app.utils.constants.ValidationConstants;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TrimValidator
public class BlogPostRequest {

	@NotBlank(message = "Title is required")
	private String title;

	@NotBlank(message = "Content is required")
	private String content;

	@NotNull(message = "Category is required")
	private BlogCategory category;

	@NotNull(message = "Image is required")
	private MultipartFile featuredImage;

//	@Min(value = 1, message = "Read time must be at least 1 minute")
//	private int readTimeMinutes;

	private Set<String> tags;

}
