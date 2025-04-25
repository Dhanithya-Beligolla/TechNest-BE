package com.example.technestbe.controller;

import com.example.technestbe.dto.UserResponse;
import com.example.technestbe.dto.UpdateProfileRequest;
import com.example.technestbe.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // Helper method to get the authenticated user's email from the SecurityContext
    private String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername(); // Email in our case
        }
        throw new IllegalStateException("User not authenticated or principal is not a UserDetails instance");
    }

    // View Profile
    @GetMapping
    public ResponseEntity<UserResponse> viewProfile() {
        String email = getAuthenticatedUserEmail();
        UserResponse userResponse = userService.findByEmail(email);
        return ResponseEntity.ok(userResponse);
    }

    // Update Profile
    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        String email = getAuthenticatedUserEmail();
        UserResponse updatedUser = userService.updateProfile(email, request);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete Profile
    @DeleteMapping
    public ResponseEntity<String> deleteProfile() {
        String email = getAuthenticatedUserEmail();
        userService.deleteProfile(email);
        return ResponseEntity.ok("Profile deleted successfully");
    }
}