package com.example.technestbe.service;

import com.example.technestbe.dto.UserRegisterRequest;
import com.example.technestbe.dto.AdminRegisterRequest;
import com.example.technestbe.dto.UserResponse;
import com.example.technestbe.dto.UpdateProfileRequest;
import com.example.technestbe.model.User;
import com.example.technestbe.repo.UserRepository;
import com.example.technestbe.util.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMobile(request.getMobile());
        user.setBirthday(request.getBirthday());
        user.setRole("USER");
        userRepository.save(user);

        return mapToUserResponse(user);
    }

    public UserResponse registerAdmin(AdminRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole("ADMIN");
        userRepository.save(user);

        return mapToUserResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        return mapToUserResponse(user);
    }

    public UserResponse registerOauthUser(String email, String name, String provider, String oauthId) {
        if (!"GOOGLE".equals(provider)) {
            throw new ApiException("Unsupported OAuth provider", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByOauthProviderAndOauthId(provider, oauthId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(name);
                    newUser.setRole("USER");
                    newUser.setOauthProvider(provider);
                    newUser.setOauthId(oauthId);
                    return userRepository.save(newUser);
                });
        return mapToUserResponse(user);
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setNewPassword(null); // Clear newPassword after reset
        userRepository.save(user);
    }

    // New method to update profile
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        // Update fields if provided in the request
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getMobile() != null) {
            user.setMobile(request.getMobile());
        }
        if (request.getBirthday() != null) {
            user.setBirthday(request.getBirthday());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        userRepository.save(user);
        return mapToUserResponse(user);
    }

    // New method to delete profile
    public void deleteProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setMobile(user.getMobile());
        response.setBirthday(user.getBirthday());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());
        response.setOauthProvider(user.getOauthProvider());
        response.setOauthId(user.getOauthId());
        return response;
    }
}