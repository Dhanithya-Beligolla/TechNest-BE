package com.example.technestbe.dto.communitymanagement;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommunityPostResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long commentCount;
}