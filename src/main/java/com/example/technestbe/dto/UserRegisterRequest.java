package com.example.technestbe.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserRegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String mobile;
    private LocalDate birthday;
}