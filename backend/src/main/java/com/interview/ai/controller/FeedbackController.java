package com.interview.ai.controller;

import com.interview.ai.dto.FeedbackResponse;
import com.interview.ai.service.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final InterviewService interviewService;

    public FeedbackController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<?> getFeedback(@PathVariable Long interviewId) {
        try {
            FeedbackResponse feedback = interviewService.getFeedback(interviewId);
            return ResponseEntity.ok(feedback);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
