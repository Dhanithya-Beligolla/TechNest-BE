package com.example.technestbe.repo.quizes;

import com.example.technestbe.model.quizes.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserIdAndTutorialId(Long userId, Long tutorialId);
    List<Enrollment> findByUserId(Long userId);
}