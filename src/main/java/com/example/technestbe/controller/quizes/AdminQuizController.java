package com.example.technestbe.controller.quizes;

import com.example.technestbe.dto.quizes.MCQRequest;
import com.example.technestbe.dto.quizes.MCQResponse;
import com.example.technestbe.dto.quizes.TutorialRequest;
import com.example.technestbe.dto.quizes.TutorialResponse;
import com.example.technestbe.mapper.QuizMapper;
import com.example.technestbe.service.quizes.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuizController {

    private final QuizService quizService;
    private final QuizMapper quizMapper;

    public AdminQuizController(QuizService quizService, QuizMapper quizMapper) {
        this.quizService = quizService;
        this.quizMapper = quizMapper;
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
}