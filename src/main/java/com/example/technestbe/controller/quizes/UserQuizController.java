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

    // Get a specific tutorial with MCQs (for taking the quiz)
    @GetMapping("/tutorials/{tutorialId}")
    public ResponseEntity<TutorialResponse> getTutorial(@PathVariable Long tutorialId) {
        Long userId = getAuthenticatedUserId();
        userQuizService.getProgress(userId, tutorialId); // Will throw exception if not enrolled
        return ResponseEntity.ok(quizService.getTutorial(tutorialId));
    }

    // Enroll in a tutorial
    @PostMapping("/tutorials/{tutorialId}/enroll")
    public ResponseEntity<String> enroll(@PathVariable Long tutorialId) {
        Long userId = getAuthenticatedUserId();
        userQuizService.enrollUser(userId, tutorialId);
        return ResponseEntity.ok("Enrolled successfully");
    }

    // Unenroll from a tutorial
    @DeleteMapping("/tutorials/{tutorialId}/unenroll")
    public ResponseEntity<String> unenroll(@PathVariable Long tutorialId) {
        Long userId = getAuthenticatedUserId();
        userQuizService.unenrollUser(userId, tutorialId);
        return ResponseEntity.ok("Unenrolled successfully");
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

    // Get the next unanswered question for a tutorial
    @GetMapping("/tutorials/{tutorialId}/next-question")
    public ResponseEntity<Long> getNextQuestion(@PathVariable Long tutorialId) {
        Long userId = getAuthenticatedUserId();
        Long nextQuestionId = userQuizService.getNextQuestionId(userId, tutorialId);
        return ResponseEntity.ok(nextQuestionId);
    }
}