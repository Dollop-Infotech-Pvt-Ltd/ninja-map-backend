package com.ninjamap.app.payload.request;

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
public class UpdateSubCategoryRequest {

	@NotBlank(message = "Subcategory name is required")
	@Size(max = 100, message = "Subcategory name must not exceed 100 characters")
	private String subCategoryName;

	private String categoryId; // Optional - allows moving subcategory to different category
}