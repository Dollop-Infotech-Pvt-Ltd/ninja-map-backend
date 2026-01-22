package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ninjamap.app.enums.StoryCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerStoryResponse {

	private String id;

	private String title;

	private String description;

	private StoryCategory category;

	private Double rating;
	
	private String location;

	private AuthorResponse author;
	
	private Boolean isApproved;

	private ArticleStatsResponse stats;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedDate;

	private Boolean isActive;
}
