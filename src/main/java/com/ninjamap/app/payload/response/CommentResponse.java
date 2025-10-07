package com.ninjamap.app.payload.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

	private String id;
	private String name;
	private String email;
	private String content;
	private LocalDateTime createdDate;
}
