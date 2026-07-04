package com.interview.ai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false, unique = true)
    private Interview interview;

    private Integer score;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String improvements;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String detailedFeedback;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
