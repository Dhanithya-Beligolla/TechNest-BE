package com.example.technestbe.repo.quizes;

import com.example.technestbe.model.quizes.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserIdAndTutorialId(Long userId, Long tutorialId);
    List<UserProgress> findByUserId(Long userId);
}