package com.example.technestbe.dto.usermanagement;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role;
}