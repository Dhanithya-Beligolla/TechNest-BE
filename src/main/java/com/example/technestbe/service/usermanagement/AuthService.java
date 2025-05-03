package com.example.technestbe.service.usermanagement;

import com.example.technestbe.dto.usermanagement.LoginRequest;
import com.example.technestbe.dto.usermanagement.LoginResponse;
import com.example.technestbe.model.user.User;
import com.example.technestbe.repo.usermanagement.UserRepository;
import com.example.technestbe.util.usermanagement.ApiException;
import com.example.technestbe.util.usermanagement.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

//    public LoginResponse login(LoginRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
//        }
//
//        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
//        LoginResponse response = new LoginResponse();
//        response.setToken(token);
//        response.setRole(user.getRole());
//        return response;
//    }
public LoginResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
        throw new BadCredentialsException("Invalid credentials");
    }

    String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
    return new LoginResponse(token, user.getRole()); // Uses the new constructor
}
}