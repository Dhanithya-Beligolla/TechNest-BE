package com.example.technestbe.repo.quizes;

import com.example.technestbe.model.quizes.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserIdAndTutorialId(Long userId, Long tutorialId);
    List<Enrollment> findByUserId(Long userId);
    List<Enrollment> findByTutorialId(Long tutorialId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.tutorial.id = :tutorialId")
    long countByTutorialId(Long tutorialId);
}