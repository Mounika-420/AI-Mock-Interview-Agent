package com.interview.ai.repository;

import com.interview.ai.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByInterviewId(Long interviewId);
}
