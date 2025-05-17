package com.example.technestbe.controller.quizes;

import com.example.technestbe.dto.quizes.MCQRequest;
import com.example.technestbe.dto.quizes.MCQResponse;
import com.example.technestbe.dto.quizes.TutorialRequest;
import com.example.technestbe.dto.quizes.TutorialResponse;
import com.example.technestbe.dto.quizes.TutorialWithEnrollmentCountResponse;
import com.example.technestbe.mapper.QuizMapper;
import com.example.technestbe.model.quizes.Enrollment;
import com.example.technestbe.repo.quizes.EnrollmentRepository;
import com.example.technestbe.service.quizes.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/quizzes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuizController {

    private final QuizService quizService;
    private final QuizMapper quizMapper;
    private final EnrollmentRepository enrollmentRepository;

    public AdminQuizController(QuizService quizService, QuizMapper quizMapper, EnrollmentRepository enrollmentRepository) {
        this.quizService = quizService;
        this.quizMapper = quizMapper;
        this.enrollmentRepository = enrollmentRepository;
    }

    @PostMapping("/tutorials")
    public ResponseEntity<TutorialResponse> createTutorial(@Valid @RequestBody TutorialRequest request) {
        return ResponseEntity.ok(quizService.createTutorial(request));
    }

    @PutMapping("/tutorials/{tutorialId}")
    public ResponseEntity<TutorialResponse> updateTutorial(@PathVariable Long tutorialId, @Valid @RequestBody TutorialRequest request) {
        return ResponseEntity.ok(quizService.updateTutorial(tutorialId, request));
    }

    @DeleteMapping("/tutorials/{tutorialId}")
    public ResponseEntity<String> deleteTutorial(@PathVariable Long tutorialId) {
        quizService.deleteTutorial(tutorialId);
        return ResponseEntity.ok("Tutorial deleted successfully");
    }

    @PostMapping("/tutorials/{tutorialId}/mcqs")
    public ResponseEntity<MCQResponse> createMCQ(@PathVariable Long tutorialId, @Valid @RequestBody MCQRequest request) {
        return ResponseEntity.ok(quizService.createMCQ(tutorialId, request));
    }

    @PutMapping("/mcqs/{mcqId}")
    public ResponseEntity<MCQResponse> updateMCQ(@PathVariable Long mcqId, @Valid @RequestBody MCQRequest request) {
        return ResponseEntity.ok(quizService.updateMCQ(mcqId, request));
    }

    @DeleteMapping("/mcqs/{mcqId}")
    public ResponseEntity<String> deleteMCQ(@PathVariable Long mcqId) {
        quizService.deleteMCQ(mcqId);
        return ResponseEntity.ok("MCQ deleted successfully");
    }

    @DeleteMapping("/tutorials/{tutorialId}/mcqs/{mcqId}")
    public ResponseEntity<String> deleteMCQFromTutorial(@PathVariable Long tutorialId, @PathVariable Long mcqId) {
        quizService.deleteMCQFromTutorial(tutorialId, mcqId);
        return ResponseEntity.ok("MCQ deleted successfully from tutorial");
    }

    @GetMapping("/tutorials")
    public ResponseEntity<List<TutorialResponse>> getAllTutorials() {
        return ResponseEntity.ok(quizService.getAllTutorials());
    }

    @GetMapping("/tutorials/{tutorialId}")
    public ResponseEntity<TutorialResponse> getTutorial(@PathVariable Long tutorialId) {
        return ResponseEntity.ok(quizService.getTutorial(tutorialId));
    }

    @GetMapping("/enrollment-activity")
    public ResponseEntity<List<EnrollmentActivityResponse>> getEnrollmentActivity() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        List<EnrollmentActivityResponse> activityResponses = enrollments.stream()
                .map(enrollment -> new EnrollmentActivityResponse(
                        enrollment.getUser().getEmail(),
                        enrollment.getTutorial().getTitle(),
                        enrollment.getTutorial().getId(),
                        enrollment.getUser().getId(),
                        enrollment.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(activityResponses);
    }

    @GetMapping("/tutorials-with-enrollment-count")
    public ResponseEntity<List<TutorialWithEnrollmentCountResponse>> getTutorialsWithEnrollmentCount() {
        List<TutorialResponse> tutorials = quizService.getAllTutorials();
        List<TutorialWithEnrollmentCountResponse> response = tutorials.stream()
                .map(tutorial -> {
                    long enrollmentCount = enrollmentRepository.countByTutorialId(tutorial.getId());
                    return new TutorialWithEnrollmentCountResponse(
                            tutorial.getId(),
                            tutorial.getTitle(),
                            tutorial.getDescription(),
                            enrollmentCount);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Inner class for Enrollment Activity Response
    public static class EnrollmentActivityResponse {
        private String userEmail;
        private String tutorialTitle;
        private Long tutorialId;
        private Long userId;
        private LocalDateTime createdAt;

        public EnrollmentActivityResponse(String userEmail, String tutorialTitle, Long tutorialId, Long userId, LocalDateTime createdAt) {
            this.userEmail = userEmail;
            this.tutorialTitle = tutorialTitle;
            this.tutorialId = tutorialId;
            this.userId = userId;
            this.createdAt = createdAt;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getTutorialTitle() {
            return tutorialTitle;
        }

        public void setTutorialTitle(String tutorialTitle) {
            this.tutorialTitle = tutorialTitle;
        }

        public Long getTutorialId() {
            return tutorialId;
        }

        public void setTutorialId(Long tutorialId) {
            this.tutorialId = tutorialId;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}