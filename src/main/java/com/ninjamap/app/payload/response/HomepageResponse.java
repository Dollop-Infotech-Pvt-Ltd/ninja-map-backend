package com.ninjamap.app.payload.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomepageResponse {
	private List<BlogListItemResponse> featuredArticles;
	private List<BlogListItemResponse> latestArticles;
	private long totalFeatured;
	private long totalLatest;
}
