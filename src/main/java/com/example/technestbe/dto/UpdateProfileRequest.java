package com.example.technestbe.dto;

import jakarta.validation.constraints.Past;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String firstName;  // For USER
    private String lastName;   // For USER
    private String mobile;     // For USER
    @Past
    private LocalDate birthday; // For USER
    private String fullName;   // For ADMIN
}