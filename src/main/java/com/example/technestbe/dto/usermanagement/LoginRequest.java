package com.example.technestbe.dto.usermanagement;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}