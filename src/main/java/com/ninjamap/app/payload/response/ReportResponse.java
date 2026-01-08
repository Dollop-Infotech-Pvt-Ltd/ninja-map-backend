package com.ninjamap.app.payload.response;

import com.ninjamap.app.enums.ReportSeverity;
import com.ninjamap.app.enums.ReportStatus;
import com.ninjamap.app.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {

    private String id;
    private ReportType reportType;
    private ReportStatus status;
    private ReportSeverity severity;
    private String title;
    private String comment;
    private Double latitude;
    private Double longitude;
    private String address;
    private String userId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime resolvedAt;
    private String deviceInfo;
    private String ipAddress;
    private Integer viewCount;
    private Integer helpfulCount;
    private Integer notHelpfulCount;
    private List<ReportCommentResponse> comments;
    private List<ReportAttachmentResponse> attachments;
}
