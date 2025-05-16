package com.example.technestbe.controller.quizes;

import com.example.technestbe.dto.quizes.TutorialResponse;
import com.example.technestbe.dto.quizes.UserAnswerRequest;
import com.example.technestbe.dto.quizes.UserProgressResponse;
import com.example.technestbe.service.quizes.QuizService;
import com.example.technestbe.service.quizes.UserQuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/quizzes")
@PreAuthorize("hasRole('USER')")
public class UserQuizController {

    private final UserQuizService userQuizService;
    private final QuizService quizService;

    public UserQuizController(UserQuizService userQuizService, QuizService quizService) {
        this.userQuizService = userQuizService;
        this.quizService = quizService;
    }

    // Helper method to get the authenticated user's ID
    private Long getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            return userQuizService.getUserIdByEmail(email);
        }
        throw new IllegalStateException("User not authenticated or principal is not a UserDetails instance");
    }

    // Get all tutorials (for display on Tutorials page)
    @GetMapping("/tutorials")
    public ResponseEntity<List<TutorialResponse>> getAllTutorials() {
        return ResponseEntity.ok(quizService.getAllTutorials());
    }

    // Enroll in a tutorial
    @PostMapping("/tutorials/{tutorialId}/enroll")
    public ResponseEntity<String> enroll(@PathVariable Long tutorialId) {
        Long userId = getAuthenticatedUserId();
        userQuizService.enrollUser(userId, tutorialId);
        return ResponseEntity.ok("Enrolled successfully");
    }

    // Submit an answer to an MCQ
    @PostMapping("/mcqs/{mcqId}/answer")
    public ResponseEntity<String> submitAnswer(@PathVariable Long mcqId, @Valid @RequestBody UserAnswerRequest request) {
        Long userId = getAuthenticatedUserId();
        userQuizService.submitAnswer(userId, mcqId, request);
        return ResponseEntity.ok("Answer submitted successfully");
    }

    // Get user dashboard (progress for all enrolled tutorials)
    @GetMapping("/dashboard")
    public ResponseEntity<List<UserProgressResponse>> getDashboard() {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(userQuizService.getUserDashboard(userId));
    }
}