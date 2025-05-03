package com.example.technestbe.controller.usermanagement;

import com.example.technestbe.dto.usermanagement.*;
import com.example.technestbe.service.usermanagement.AuthService;
import com.example.technestbe.service.usermanagement.OtpService;
import com.example.technestbe.service.usermanagement.UserService;
import com.example.technestbe.util.usermanagement.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Received login request for email: {}", request.getEmail());
        LoginResponse loginResponse = authService.login(request);
        logger.info("Returning login response with token for email: {}", request.getEmail());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register/user")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse user = userService.registerUser(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        UserResponse user = userService.registerAdmin(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPasswordRequest(@RequestBody PasswordResetRequest request) {
        otpService.generateAndSendOtp(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<Void> resetPasswordConfirm(@RequestBody PasswordResetConfirmRequest request) {
        otpService.validateOtp(request.getEmail(), request.getOtp());
        userService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponse user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponse updatedUser = userService.updateProfile(email, request);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userService.deleteProfile(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/registration-activity")
    public ResponseEntity<List<UserResponse>> getRegistrationActivity() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("createdAt").descending());
        Page<UserResponse> recentUsers = userService.findRecentUsers(pageable);
        return ResponseEntity.ok(recentUsers.getContent());
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(
            org.springframework.http.converter.HttpMessageNotReadableException ex) {
        logger.error("JSON parsing error: {}", ex.getMessage());
        return new ResponseEntity<>("Invalid request payload: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}