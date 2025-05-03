package com.example.technestbe.repo.quizes;

import com.example.technestbe.model.quizes.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
}