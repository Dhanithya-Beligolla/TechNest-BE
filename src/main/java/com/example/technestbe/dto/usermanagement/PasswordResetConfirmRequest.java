package com.example.technestbe.dto.usermanagement;

import lombok.Data;

@Data
public class PasswordResetConfirmRequest {
    private String email;
    private String otp;
    private String newPassword;
}