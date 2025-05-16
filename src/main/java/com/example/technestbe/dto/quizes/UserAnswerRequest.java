package com.example.technestbe.dto.quizes;

public class UserAnswerRequest {

    private Long mcqId;
    private String userAnswer;

    // Getters and Setters
    public Long getMcqId() {
        return mcqId;
    }

    public void setMcqId(Long mcqId) {
        this.mcqId = mcqId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}