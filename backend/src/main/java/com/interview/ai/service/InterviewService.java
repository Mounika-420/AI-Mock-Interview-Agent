package com.interview.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.ai.dto.FeedbackResponse;
import com.interview.ai.dto.InterviewRequest;
import com.interview.ai.entity.Feedback;
import com.interview.ai.entity.Interview;
import com.interview.ai.entity.User;
import com.interview.ai.repository.FeedbackRepository;
import com.interview.ai.repository.InterviewRepository;
import com.interview.ai.repository.UserRepository;
import com.interview.ai.util.PromptBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InterviewService(InterviewRepository interviewRepository,
                             FeedbackRepository feedbackRepository,
                             UserRepository userRepository,
                             GeminiService geminiService) {
        this.interviewRepository = interviewRepository;
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.geminiService = geminiService;
    }

    /** Generates questions via Gemini and persists a new Interview. */
    public Interview startInterview(InterviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        String prompt = PromptBuilder.buildQuestionGenerationPrompt(
                request.getRole(),
                request.getTechStack(),
                request.getDifficulty(),
                request.getNumberOfQuestions() == null ? 5 : request.getNumberOfQuestions()
        );

        String rawResponse = geminiService.generateContent(prompt);
        String questionsJson = geminiService.cleanJson(rawResponse);

        Interview interview = Interview.builder()
                .user(user)
                .role(request.getRole())
                .techStack(request.getTechStack())
                .difficulty(request.getDifficulty() == null ? "Medium" : request.getDifficulty())
                .questions(questionsJson)
                .status("IN_PROGRESS")
                .build();

        return interviewRepository.save(interview);
    }

    public Interview getInterview(Long id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Interview not found: " + id));
    }

    public List<Interview> getInterviewsForUser(Long userId) {
        return interviewRepository.findByUserId(userId);
    }

    /**
     * Accepts the candidate's answers, asks Gemini to evaluate them,
     * stores a Feedback entity and marks the interview as completed.
     */
    @SuppressWarnings("unchecked")
    public FeedbackResponse submitAnswers(Long interviewId, List<String> answers) {
        Interview interview = getInterview(interviewId);

        String answersJson;
        try {
            answersJson = objectMapper.writeValueAsString(answers);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize answers", e);
        }
        interview.setAnswers(answersJson);
        interview.setStatus("COMPLETED");
        interviewRepository.save(interview);

        String prompt = PromptBuilder.buildFeedbackPrompt(
                interview.getRole(), interview.getTechStack(),
                interview.getQuestions(), answersJson
        );

        String rawResponse = geminiService.generateContent(prompt);
        String cleanJson = geminiService.cleanJson(rawResponse);

        try {
            Map<String, Object> parsed = objectMapper.readValue(cleanJson, Map.class);

            Feedback feedback = Feedback.builder()
                    .interview(interview)
                    .score((Integer) parsed.get("score"))
                    .strengths(objectMapper.writeValueAsString(parsed.get("strengths")))
                    .improvements(objectMapper.writeValueAsString(parsed.get("improvements")))
                    .detailedFeedback(String.valueOf(parsed.get("detailedFeedback")))
                    .build();

            feedback = feedbackRepository.save(feedback);

            return FeedbackResponse.builder()
                    .interviewId(interview.getId())
                    .score(feedback.getScore())
                    .strengths((List<String>) parsed.get("strengths"))
                    .improvements((List<String>) parsed.get("improvements"))
                    .detailedFeedback(feedback.getDetailedFeedback())
                    .generatedAt(feedback.getCreatedAt())
                    .build();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Gemini feedback response: " + cleanJson, e);
        }
    }

    public FeedbackResponse getFeedback(Long interviewId) {
        Feedback feedback = feedbackRepository.findByInterviewId(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found for interview: " + interviewId));

        try {
            List<String> strengths = objectMapper.readValue(feedback.getStrengths(), List.class);
            List<String> improvements = objectMapper.readValue(feedback.getImprovements(), List.class);

            return FeedbackResponse.builder()
                    .interviewId(feedback.getInterview().getId())
                    .score(feedback.getScore())
                    .strengths(strengths)
                    .improvements(improvements)
                    .detailedFeedback(feedback.getDetailedFeedback())
                    .generatedAt(feedback.getCreatedAt())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read stored feedback", e);
        }
    }
}
