package com.example.technestbe.dto.usermanagement;

public class LoginResponse {
    private String token;
    private String role;

    // Default no-args constructor (required for Jackson deserialization)
    public LoginResponse() {
    }

    // Constructor with token and role parameters
    public LoginResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}