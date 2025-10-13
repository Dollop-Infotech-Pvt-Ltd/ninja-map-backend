package com.ninjamap.app.payload.request;

import com.ninjamap.app.utils.annotations.TrimValidator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TrimValidator
public class CommentRequest {

	@NotBlank(message = "Content is required")
	@Size(min = 10, message = "Content must be at least 10 characters long")
	private String content;

	// Optional parent comment ID for replies
	private String parentCommentId;
}
