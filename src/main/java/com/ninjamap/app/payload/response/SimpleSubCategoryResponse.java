package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleSubCategoryResponse {

	private String id;

	private String subCategoryName;

	private Boolean isActive;

	private LocalDateTime createdDate;

	private LocalDateTime updatedDate;
}