package com.example.technestbe.repo.quizes;

import com.example.technestbe.model.quizes.MCQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MCQRepository extends JpaRepository<MCQ, Long> {
}