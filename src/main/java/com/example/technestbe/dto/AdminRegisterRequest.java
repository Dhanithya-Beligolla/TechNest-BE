package com.example.technestbe.dto;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String email;
    private String password;
    private String fullName;
}