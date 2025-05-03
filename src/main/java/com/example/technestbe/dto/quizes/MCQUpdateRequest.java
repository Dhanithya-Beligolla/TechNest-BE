package com.example.technestbe.dto.quizes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class MCQUpdateRequest {

    @NotBlank(message = "Question is required")
    private String question;

    @NotEmpty(message = "Options cannot be empty")
    @Size(min = 2, max = 4, message = "Options must be between 2 and 4")
    private List<String> options;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}