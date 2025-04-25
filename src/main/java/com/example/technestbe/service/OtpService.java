package com.example.technestbe.service;

import com.example.technestbe.util.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private EmailService emailService;

    @Value("${otp.expiration}")
    private long otpExpiration;

    private static final String OTP_PREFIX = "otp:user:";

    public void generateAndSendOtp(String email) {
        String otp = generateOtp();
        redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, otpExpiration, TimeUnit.MILLISECONDS);
        emailService.sendOtpEmail(email, otp);
    }

    public void validateOtp(String email, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + email);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new ApiException("Invalid or expired OTP", HttpStatus.BAD_REQUEST);
        }
        redisTemplate.delete(OTP_PREFIX + email);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }
}