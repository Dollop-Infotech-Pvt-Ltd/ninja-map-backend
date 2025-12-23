package com.ninjamap.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportCommentResponse {

    private String id;
    private String reportId;
    private String userId;
    private String comment;
    private Boolean isAdminComment;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
