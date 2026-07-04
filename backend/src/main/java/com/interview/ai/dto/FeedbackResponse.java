package com.interview.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {

    private Long interviewId;
    private Integer score;
    private List<String> strengths;
    private List<String> improvements;
    private String detailedFeedback;
    private LocalDateTime generatedAt;
}
