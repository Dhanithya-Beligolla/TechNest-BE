package com.example.technestbe.dto;

import lombok.Data;

@Data
public class PasswordResetConfirmRequest {
    private String email;
    private String otp;
    private String newPassword;
}