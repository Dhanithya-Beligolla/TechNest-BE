package com.example.technestbe.repo.quizes;

import com.example.technestbe.model.quizes.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByUserIdAndTutorialId(Long userId, Long tutorialId);
    void deleteByUserIdAndTutorialId(Long userId, Long tutorialId);
}