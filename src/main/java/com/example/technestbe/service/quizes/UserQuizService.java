package com.example.technestbe.service.quizes;

import com.example.technestbe.dto.quizes.UserAnswerRequest;
import com.example.technestbe.dto.quizes.UserProgressResponse;
import com.example.technestbe.model.quizes.Enrollment;
import com.example.technestbe.model.quizes.MCQ;
import com.example.technestbe.model.quizes.Tutorial;
import com.example.technestbe.model.quizes.UserAnswer;
import com.example.technestbe.model.user.User;
import com.example.technestbe.repo.quizes.EnrollmentRepository;
import com.example.technestbe.repo.quizes.MCQRepository;
import com.example.technestbe.repo.quizes.TutorialRepository;
import com.example.technestbe.repo.quizes.UserAnswerRepository;
import com.example.technestbe.repo.usermanagement.UserRepository;
import com.example.technestbe.util.usermanagement.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserQuizService {

    private final TutorialRepository tutorialRepository;
    private final MCQRepository mcqRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public UserQuizService(TutorialRepository tutorialRepository, MCQRepository mcqRepository,
                           UserAnswerRepository userAnswerRepository, EnrollmentRepository enrollmentRepository,
                           UserRepository userRepository) {
        this.tutorialRepository = tutorialRepository;
        this.mcqRepository = mcqRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        return user.getId();
    }

    @Transactional
    public void enrollUser(Long userId, Long tutorialId) {
        // Fetch the tutorial and verify its existence with a fresh query
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found with ID: " + tutorialId, HttpStatus.NOT_FOUND));

        // Use a native query to validate the tutorial exists in the database
        Long count = (Long) entityManager.createNativeQuery("SELECT COUNT(*) FROM tutorial WHERE id = ?")
                .setParameter(1, tutorialId)
                .getSingleResult();
        if (count == 0) {
            throw new ApiException("Tutorial no longer exists with ID: " + tutorialId, HttpStatus.NOT_FOUND);
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByUserIdAndTutorialId(userId, tutorialId);
        if (existingEnrollment.isPresent()) {
            throw new ApiException("User already enrolled in this tutorial", HttpStatus.BAD_REQUEST);
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(userRepository.getReferenceById(userId));
        enrollment.setTutorial(tutorial);
        try {
            enrollmentRepository.save(enrollment);
        } catch (Exception e) {
            throw new ApiException("Failed to enroll user due to database error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void unenrollUser(Long userId, Long tutorialId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndTutorialId(userId, tutorialId)
                .orElseThrow(() -> new ApiException("User is not enrolled in this tutorial", HttpStatus.NOT_FOUND));

        // Optionally delete associated user answers (if desired)
        userAnswerRepository.deleteByUserIdAndTutorialId(userId, tutorialId);

        enrollmentRepository.delete(enrollment);
    }

    @Transactional
    public void submitAnswer(Long userId, Long mcqId, UserAnswerRequest request) {
        MCQ mcq = mcqRepository.findById(mcqId)
                .orElseThrow(() -> new ApiException("MCQ not found", HttpStatus.NOT_FOUND));

        Tutorial tutorial = mcq.getTutorial();
        if (tutorial == null) {
            throw new ApiException("MCQ is not associated with any tutorial", HttpStatus.BAD_REQUEST);
        }

        Optional<Enrollment> enrollment = enrollmentRepository.findByUserIdAndTutorialId(userId, tutorial.getId());
        if (enrollment.isEmpty()) {
            throw new ApiException("User is not enrolled in this tutorial", HttpStatus.BAD_REQUEST);
        }

        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setUserId(userId);
        userAnswer.setTutorial(tutorial);
        userAnswer.setMcq(mcq);
        userAnswer.setUserAnswer(request.getUserAnswer());

        int correctAnswerIndex = mcq.getCorrectAnswer();
        String correctAnswer = mcq.getOptions().get(correctAnswerIndex);
        userAnswer.setCorrect(request.getUserAnswer().equals(correctAnswer));

        userAnswerRepository.save(userAnswer);
    }

    @Transactional(readOnly = true)
    public UserProgressResponse getProgress(Long userId, Long tutorialId) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found", HttpStatus.NOT_FOUND));

        List<UserAnswer> userAnswers = userAnswerRepository.findByUserIdAndTutorialId(userId, tutorialId);

        int totalQuestions = tutorial.getMcqs().size();
        int correctAnswers = (int) userAnswers.stream().filter(UserAnswer::isCorrect).count();

        UserProgressResponse response = new UserProgressResponse();
        response.setTutorialId(tutorialId);
        response.setTotalQuestions(totalQuestions);
        response.setCorrectAnswers(correctAnswers);

        return response;
    }

    @Transactional(readOnly = true)
    public List<UserProgressResponse> getUserDashboard(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);

        return enrollments.stream()
                .map(enrollment -> getProgress(userId, enrollment.getTutorial().getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getNextQuestionId(Long userId, Long tutorialId) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found", HttpStatus.NOT_FOUND));

        Optional<Enrollment> enrollment = enrollmentRepository.findByUserIdAndTutorialId(userId, tutorialId);
        if (enrollment.isEmpty()) {
            throw new ApiException("User is not enrolled in this tutorial", HttpStatus.BAD_REQUEST);
        }

        List<UserAnswer> userAnswers = userAnswerRepository.findByUserIdAndTutorialId(userId, tutorialId);
        List<Long> answeredMcqIds = userAnswers.stream()
                .map(userAnswer -> userAnswer.getMcq().getId())
                .collect(Collectors.toList());

        // Find the first MCQ that hasn't been answered
        for (MCQ mcq : tutorial.getMcqs()) {
            if (!answeredMcqIds.contains(mcq.getId())) {
                return mcq.getId();
            }
        }

        // If all questions are answered, return -1 to indicate completion
        return -1L;
    }
}