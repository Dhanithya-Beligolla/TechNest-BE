package com.example.technestbe.dto.advertisement;

import lombok.Data;

@Data
public class AdvertisementResponseDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String referenceName;
    private String link;
    private String createdBy;
}
