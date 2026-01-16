package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class CustomerStoryDetailResponse {

	private String id;

	private String title;

	private String description;

	private StoryCategory category;

	private Double rating;

	private Integer readTimeMinutes;

	private String companyName;

	private String location;

	private Integer timeSavedPercentage;

	private Integer costReductionPercentage;

	private Integer satisfactionPercentage;

	private AuthorResponse author;

	private ArticleStatsResponse stats;

	private Boolean isLiked;

	private Boolean isSaved;

	private List<CommentResponse> comments;

	private List<CustomerStoryResponse> relatedStories;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedDate;

	private Boolean isActive;
}
