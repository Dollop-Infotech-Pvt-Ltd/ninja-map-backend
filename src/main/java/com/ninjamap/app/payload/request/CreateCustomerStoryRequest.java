package com.ninjamap.app.payload.request;

import org.springframework.web.multipart.MultipartFile;

import com.ninjamap.app.enums.StoryCategory;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCustomerStoryRequest {

	@NotBlank(message = "Title is required")
	@Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
	private String title;

	@NotBlank(message = "Description is required")
	@Size(min = 50, max = 5000, message = "Description must be between 50 and 5000 characters")
	private String description;

	@NotNull(message = "Category is required")
	private StoryCategory category;
	
	@Column(columnDefinition = "TEXT")
	@NotNull(message = "Category is required")
	private String authorBio;
	
	@Column(columnDefinition = "TEXT")
	@NotNull(message = "Email is required")
	private String authorEmail;
	
	@NotNull(message = "Organisation name is required")
	private String organisationName;
	
	@Column(columnDefinition = "TEXT")
	@NotNull(message = "Name is required")
	private String authorName;
	
	@Size(max = 255, message = "Location must not exceed 255 characters")
	@NotNull(message = "Location is required")
	private String location;

	private MultipartFile authorProfilePic;
}
