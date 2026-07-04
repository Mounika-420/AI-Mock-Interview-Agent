package com.interview.ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String techStack;

    private String difficulty;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String questions;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String answers;

    @Builder.Default
    private String status = "IN_PROGRESS";

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}