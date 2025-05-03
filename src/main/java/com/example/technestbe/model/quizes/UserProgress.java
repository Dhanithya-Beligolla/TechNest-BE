package com.example.technestbe.model.quizes;

import com.example.technestbe.model.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_progress")
@Data
public class UserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tutorial_id", nullable = false)
    private Tutorial tutorial;

    @Column(nullable = false)
    private double completionPercentage; // 0 to 100

    @Column(nullable = false)
    private int totalScore; // Sum of scores from answered MCQs
}