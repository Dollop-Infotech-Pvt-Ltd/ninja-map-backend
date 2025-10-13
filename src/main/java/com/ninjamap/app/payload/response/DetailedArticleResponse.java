package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.ninjamap.app.enums.BlogCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailedArticleResponse {
	private String id;
	private String title;
	private BlogCategory category;
	private String previewContent;
	private String detailedContent;
	private String thumbnailUrl;
	private String featuredImageUrl;
	private Integer readTimeMinutes;
	private LocalDateTime publishedDate;
	private AuthorResponse author;
	private ArticleStatsResponse stats;
	private Set<String> tags;
	private List<CommentResponse> comments;
	private List<BlogListItemResponse> relatedArticles;
}
