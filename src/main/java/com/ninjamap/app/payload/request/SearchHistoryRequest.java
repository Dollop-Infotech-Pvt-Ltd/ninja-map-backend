package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.constants.ValidationConstants;

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

	@NotBlank(message = ValidationConstants.SEARCH_TERM_REQUIRED)
	@Size(min = 1, max = 255, message = ValidationConstants.SEARCH_TERM_SIZE)
	private String searchTerm;

}
