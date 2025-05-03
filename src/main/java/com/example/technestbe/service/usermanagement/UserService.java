package com.example.technestbe.service.usermanagement;

import com.example.technestbe.dto.usermanagement.*;
import com.example.technestbe.util.usermanagement.ApiException;
import com.example.technestbe.model.user.User;
import com.example.technestbe.repo.usermanagement.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse registerUser(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email already exists", HttpStatus.BAD_REQUEST); // Fixed: String first, then HttpStatus
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
            throw new ApiException("Email already exists", HttpStatus.BAD_REQUEST); // Fixed: String first, then HttpStatus
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
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND)); // Fixed: String first, then HttpStatus
        return mapToUserResponse(user);
    }

    public UserResponse registerOauthUser(String email, String name, String provider, String oauthId) {
        User user = userRepository.findByOauthProviderAndOauthId(provider, oauthId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setOauthProvider(provider);
                    newUser.setOauthId(oauthId);
                    newUser.setRole("USER");
                    return userRepository.save(newUser);
                });
        return mapToUserResponse(user);
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND)); // Fixed: String first, then HttpStatus
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND)); // Fixed: String first, then HttpStatus
        if (user.getRole().equals("USER")) {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setMobile(request.getMobile());
            user.setBirthday(request.getBirthday());
        } else {
            user.setFullName(request.getFullName());
        }
        userRepository.save(user);
        return mapToUserResponse(user);
    }

    public void deleteProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND)); // Fixed: String first, then HttpStatus
        userRepository.delete(user);
    }

    public Page<UserResponse> findRecentUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToUserResponse);
    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
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
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}