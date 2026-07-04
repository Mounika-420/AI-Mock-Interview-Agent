package com.interview.ai.util;

/**
 * Builds the text prompts sent to the Gemini API for question generation
 * and for evaluating a candidate's answers.
 */
public class PromptBuilder {

    private PromptBuilder() {
    }

    public static String buildQuestionGenerationPrompt(String role, String techStack,
                                                         String difficulty, int numberOfQuestions) {
        return """
                You are an experienced technical interviewer.
                Generate %d interview questions for a candidate applying for the role of "%s".
                The candidate's tech stack is: %s.
                Difficulty level: %s.

                Return ONLY a valid JSON array of strings, with no markdown formatting and no extra text.
                Example format: ["Question 1", "Question 2", "Question 3"]
                """.formatted(numberOfQuestions, role, techStack, difficulty == null ? "Medium" : difficulty);
    }

    public static String buildFeedbackPrompt(String role, String techStack, String questionsJson, String answersJson) {
        return """
                You are an experienced technical interviewer evaluating a mock interview.
                Role: %s
                Tech Stack: %s

                Questions asked (JSON array): %s
                Candidate's answers (JSON array, same order): %s

                Evaluate the candidate's performance and return ONLY valid JSON (no markdown, no extra text)
                in exactly this shape:
                {
                  "score": <integer 0-100>,
                  "strengths": ["...", "..."],
                  "improvements": ["...", "..."],
                  "detailedFeedback": "..."
                }
                """.formatted(role, techStack, questionsJson, answersJson);
    }
}
