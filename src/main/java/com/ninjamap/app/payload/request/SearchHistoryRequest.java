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
public class SearchHistoryRequest {

	@NotBlank(message = "Search term is required")
	@Size(min = 1, max = 255, message = "Search term must be between 1 and 255 characters")
	private String searchTerm;

	@NotNull(message = "Search type is required")
	private String searchType;
}
