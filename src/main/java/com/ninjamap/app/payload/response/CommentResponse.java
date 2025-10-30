package com.ninjamap.app.payload.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

	private String id;
	private String name;
	private String designation;
	private String profilePicture;
	private String content;
	private LocalDateTime createdDate;
	private Integer likeCount;
	private Boolean isLike;
	private List<CommentResponse> replies;
}
