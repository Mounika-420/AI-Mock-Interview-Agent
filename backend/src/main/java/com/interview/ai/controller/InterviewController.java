package com.interview.ai.controller;

import com.interview.ai.dto.FeedbackResponse;
import com.interview.ai.dto.InterviewRequest;
import com.interview.ai.entity.Interview;
import com.interview.ai.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startInterview(@Valid @RequestBody InterviewRequest request) {
        try {
            Interview interview = interviewService.startInterview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(interview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInterview(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(interviewService.getInterview(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getInterviewsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(interviewService.getInterviewsForUser(userId));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitAnswers(@PathVariable Long id, @RequestBody List<String> answers) {
        try {
            FeedbackResponse feedback = interviewService.submitAnswers(id, answers);
            return ResponseEntity.ok(feedback);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
