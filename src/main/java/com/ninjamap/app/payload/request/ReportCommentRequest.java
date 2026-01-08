package com.ninjamap.app.payload.request;

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
public class ReportCommentRequest {

    @NotBlank(message = "Comment is required")
    @Size(min = 1, max = 5000, message = "Comment must be between 1 and 5000 characters")
    private String comment;
}
