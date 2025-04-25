package com.example.technestbe.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role;
}