package com.ninjamap.app.payload.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlogHomeResponse {
	private Integer totalPosts;
	private Map<String, Integer> categoryCounts; // e.g., Navigation -> 15, Technology -> 12
	private BlogListItemResponse featuredArticle;
	private List<BlogListItemResponse> latestArticles;
}
