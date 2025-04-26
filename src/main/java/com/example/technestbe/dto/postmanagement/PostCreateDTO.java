package com.example.technestbe.dto.postmanagement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class PostCreateDTO {
    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 1000)
    private String description;

    @Size(max = 3)
    private MultipartFile[] mediaFiles;

    // Getters and Setters
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

    public MultipartFile[] getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(MultipartFile[] mediaFiles) {
        this.mediaFiles = mediaFiles;
    }
}