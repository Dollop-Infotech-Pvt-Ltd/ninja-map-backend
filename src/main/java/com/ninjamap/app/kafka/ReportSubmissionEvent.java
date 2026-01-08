package com.ninjamap.app.kafka;

import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportSubmissionEvent {

	private String reportId;
	private ReportType reportType;
	private ReportSeverity severity;
	private String title;
	private String description;
	private Double latitude;
	private Double longitude;
	private String address;
	private String userId;
	private LocalDateTime createdAt;
	private String eventType = "REPORT_SUBMITTED";
}
