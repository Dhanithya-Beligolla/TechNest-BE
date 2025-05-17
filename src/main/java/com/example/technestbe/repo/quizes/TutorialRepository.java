package com.example.technestbe.repo.quizes;

import com.example.technestbe.model.quizes.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
    @Query("SELECT COUNT(t) FROM Tutorial t WHERE t.id = :id")
    Long countById(Long id);
}