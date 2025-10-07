package com.ninjamap.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionAnswer {
    private String question;
    @Column(columnDefinition = "TEXT")
    private String answer;
}
