package com.example.technestbe.model.user;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName; // For USER
    private String lastName;  // For USER
    private String mobile;    // For USER
    private LocalDate birthday; // For USER
    private String fullName;  // For ADMIN

    @Column(nullable = false)
    private String role; // "USER" or "ADMIN"

    private String oauthProvider; // "GOOGLE", "FACEBOOK", or null
    private String oauthId;      // OAuth provider ID

    private String otp;          // For OTP functionality
    private String newPassword;  // For password reset
    private String token;        // For authentication token

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}