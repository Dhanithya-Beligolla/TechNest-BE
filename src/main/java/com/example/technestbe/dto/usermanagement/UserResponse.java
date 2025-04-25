package com.example.technestbe.dto.usermanagement;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String mobile;
    private LocalDate birthday;
    private String fullName;
    private String role;
    private String oauthProvider; // Added
    private String oauthId;      // Added
}