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
public class BlogPostResponse {
	private String id;
	private String title;
	private String content;
	private BlogCategory category;
	private String featuredImageUrl;
	private LocalDateTime publishedDate;
	private Integer readTimeMinutes;

	private Integer views;
	private Integer likes;
	private Integer comments;
	private Integer shares;

	private String authorName;
	private String authorRole;
	private Set<String> tags;
	private List<CommentResponse> commentsList;
}
