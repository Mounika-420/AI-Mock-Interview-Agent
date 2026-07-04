package com.interview.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String role;          // e.g. "Backend Developer"

    @NotBlank
    private String techStack;     // e.g. "Java, Spring Boot, MySQL"

    private String difficulty;    // Easy | Medium | Hard

    private Integer numberOfQuestions = 5;
}
