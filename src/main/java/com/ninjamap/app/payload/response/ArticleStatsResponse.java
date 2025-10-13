package com.ninjamap.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleStatsResponse {
	private Integer views;
	private Integer likes;
	private Integer comments;
	private Integer shares;
}
