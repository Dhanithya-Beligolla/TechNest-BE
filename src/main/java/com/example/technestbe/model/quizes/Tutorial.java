package com.example.technestbe.model.quizes;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MCQ> mcqs = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MCQ> getMcqs() {
        return mcqs;
    }

    public void setMcqs(List<MCQ> mcqs) {
        this.mcqs = mcqs;
    }
}