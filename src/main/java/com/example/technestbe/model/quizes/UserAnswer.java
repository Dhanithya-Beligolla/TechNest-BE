package com.example.technestbe.model.quizes;

import jakarta.persistence.*;

@Entity
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumn(name = "tutorial_id")
    private Tutorial tutorial;

    @ManyToOne
    @JoinColumn(name = "mcq_id")
    private MCQ mcq;

    private String userAnswer;

    private boolean isCorrect;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Tutorial getTutorial() {
        return tutorial;
    }

    public void setTutorial(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    public MCQ getMcq() {
        return mcq;
    }

    public void setMcq(MCQ mcq) {
        this.mcq = mcq;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}