package com.example.technestbe.service.usermanagement;

import com.example.technestbe.util.usermanagement.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.time.Duration;

@Service
public class OtpService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final long otpExpiration;

    public OtpService(JavaMailSender mailSender, RedisTemplate<String, String> redisTemplate,
                      @Value("${otp.expiration}") long otpExpiration) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
        this.otpExpiration = otpExpiration;
    }

    public void generateAndSendOtp(String email) {
        // Generate OTP
        String otp = String.valueOf((int) (Math.random() * 345678) + 263975);

        // Log the OTP for debugging
        System.out.println("Generated OTP for " + email + ": " + otp);

        // Test Redis connection
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            System.out.println("Successfully connected to Redis");
        } catch (Exception e) {
            System.err.println("Failed to connect to Redis: " + e.getMessage());
            throw new ApiException("Unable to connect to Redis: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Store OTP in Redis with expiration
        redisTemplate.opsForValue().set(email, otp, Duration.ofMillis(otpExpiration));

        // Send OTP via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp);
        try {
            mailSender.send(message);
            System.out.println("OTP email sent successfully to " + email);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email to " + email + ": " + e.getMessage());
            throw new ApiException("Failed to send OTP email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void validateOtp(String email, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new ApiException("Invalid or expired OTP", HttpStatus.BAD_REQUEST);
        }
        redisTemplate.delete(email);
    }
}