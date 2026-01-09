package com.ninjamap.app.payload.request;

import jakarta.validation.constraints.NotBlank;
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
public class SubCategoryRequest {

	@NotBlank(message = "Subcategory name is required")
	@Size(max = 100, message = "Subcategory name must not exceed 100 characters")
	private String subCategoryName;

	@NotNull(message = "Category ID is required")
	@NotBlank(message = "Category ID cannot be blank")
	private String categoryId;
}