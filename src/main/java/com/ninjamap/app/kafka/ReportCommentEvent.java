package com.ninjamap.app.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportCommentEvent {

	private String commentId;
	private String reportId;
	private String userId;
	private String comment;
	private Boolean isAdminComment;
	private LocalDateTime createdAt;
	private String eventType = "REPORT_COMMENT_ADDED";
}
