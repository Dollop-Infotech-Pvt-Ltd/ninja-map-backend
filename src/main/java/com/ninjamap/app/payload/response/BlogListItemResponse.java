package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;

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
public class BlogListItemResponse {
	private String id;
	private BlogCategory category;
	private String title;
	private String previewContent;
	private String thumbnailUrl;
	private Integer readTimeMinutes;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime postDate;
	private Integer views;
	private Integer likes;
	private AuthorResponse author;
}
