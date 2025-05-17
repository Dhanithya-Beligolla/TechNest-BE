package com.example.technestbe.dto.quizes;

public class TutorialWithEnrollmentCountResponse {

    private Long id;
    private String title;
    private String description;
    private long enrollmentCount;

    public TutorialWithEnrollmentCountResponse(Long id, String title, String description, long enrollmentCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.enrollmentCount = enrollmentCount;
    }

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

    public long getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(long enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }
}