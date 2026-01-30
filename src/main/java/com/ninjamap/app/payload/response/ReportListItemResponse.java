package com.ninjamap.app.payload.response;

import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportStatus;
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
public class ReportListItemResponse {

    private String id;
    private ReportType reportType;
    private ReportStatus status;
    private ReportSeverity severity;
    private String comment;
    private Double latitude;
    private Double longitude;
    private String address;
    private String userId;
    private String fullName;
    private String profilePicture;
    private String reportPicture;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
