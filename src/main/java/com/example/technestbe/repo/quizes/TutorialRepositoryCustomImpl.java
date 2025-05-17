package com.example.technestbe.repo.quizes;

import com.example.technestbe.model.quizes.Tutorial;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class TutorialRepositoryCustomImpl implements TutorialRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void refresh(Tutorial tutorial) {
        entityManager.refresh(tutorial);
    }
}