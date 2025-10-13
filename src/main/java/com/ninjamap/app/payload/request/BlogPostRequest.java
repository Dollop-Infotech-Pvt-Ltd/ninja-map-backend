package com.ninjamap.app.payload.request;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.BlogCategory;
import com.ninjamap.app.utils.annotations.TrimValidator;

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

	@NotBlank(message = "Preview content is required")
	private String previewContent; // shown in list view

	@NotBlank(message = "Detailed content is required")
	private String detailedContent; // full article content

	@NotNull(message = "Category is required")
	private BlogCategory category;

	private Boolean isFeaturedArticle;

	private MultipartFile featuredImage; // Optional featured image

	private MultipartFile thumbnailImage; // Optional thumbnail image

	private Set<String> tags;
}
