package com.example.technestbe.controller.usermanagement;

import com.example.technestbe.dto.usermanagement.*;
import com.example.technestbe.service.usermanagement.AuthService;
import com.example.technestbe.service.usermanagement.OtpService;
import com.example.technestbe.service.usermanagement.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final OtpService otpService;

    public AuthController(AuthService authService, UserService userService, OtpService otpService) {
        this.authService = authService;
        this.userService = userService;
        this.otpService = otpService;
    }

    @PostMapping("/register/user")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(userService.registerAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        otpService.generateAndSendOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        otpService.validateOtp(request.getEmail(), request.getOtp());
        userService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}