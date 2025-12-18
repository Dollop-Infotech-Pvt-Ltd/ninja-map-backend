package com.ninjamap.app.payload.request;

import org.springframework.web.multipart.MultipartFile;

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
public class CategoryRequest {

	@NotBlank(message = "Category name is required")
	@Size(max = 100, message = "Category name must not exceed 100 characters")
	private String categoryName;

	private MultipartFile categoryPicture;
}
