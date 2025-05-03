package com.example.technestbe.controller.usermanagement;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private final JavaMailSender mailSender;

    public TestController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendTestEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Test Email");
        message.setText("This is a test email from TechNest-BE");
        try {
            mailSender.send(message);
            return ResponseEntity.ok("Test email sent");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send test email: " + e.getMessage());
        }
    }
}