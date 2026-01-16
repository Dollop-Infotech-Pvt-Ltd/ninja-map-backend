package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubCategoryResponse {

	private String id;

	private String subCategoryName;

	private String categoryId;

	private String categoryName;

	private Boolean isActive;

	private LocalDateTime createdDate;


	private LocalDateTime updatedDate;
}