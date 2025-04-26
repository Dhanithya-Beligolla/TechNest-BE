package com.example.technestbe.dto.usermanagement;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String email;
    private String password;
    private String fullName;
}