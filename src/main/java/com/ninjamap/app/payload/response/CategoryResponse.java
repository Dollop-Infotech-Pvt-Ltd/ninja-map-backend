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
public class CategoryResponse {

	private String id;

	private String categoryName;

	private String categoryPicture;

	private Boolean isActive;

	private LocalDateTime createdDate;

	private LocalDateTime updatedDate;
}
