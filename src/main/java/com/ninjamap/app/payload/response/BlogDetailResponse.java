package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ninjamap.app.enums.BlogCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogDetailResponse {
	private String id;
	private BlogCategory category;
	private String title;
	private String previewContent;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime postDate;
	private Integer readTimeMinutes;

	private AuthorResponse author;
	private String featuredImageUrl;
	private String detailedContent;
	private Set<String> tags;
	private Boolean isLike;
	private Boolean isSave;

	private List<CommentResponse> comments; // include likes per comment if needed

	private ArticleStatsResponse stats; // views, likes, comments, shares

	private List<BlogListItemResponse> relatedArticles; // related articles for display
}
