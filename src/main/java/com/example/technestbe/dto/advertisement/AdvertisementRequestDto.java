package com.example.technestbe.dto.advertisement;


import lombok.Data;

@Data
public class AdvertisementRequestDto {
    private String title;
    private String description;
    private String category;
    private String referenceName;
    private String link;
}
