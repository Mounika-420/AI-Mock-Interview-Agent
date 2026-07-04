package com.interview.ai.repository;

import com.interview.ai.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUserId(Long userId);
}
