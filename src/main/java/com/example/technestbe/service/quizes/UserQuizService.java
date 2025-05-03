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

import java.util.List;
import java.util.Optional; // Added import for Optional
import java.util.stream.Collectors;

@Service
public class UserQuizService {

    private final TutorialRepository tutorialRepository;
    private final MCQRepository mcqRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

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
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found", HttpStatus.NOT_FOUND));

        userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByUserIdAndTutorialId(userId, tutorialId);
        if (existingEnrollment.isPresent()) {
            throw new ApiException("User already enrolled in this tutorial", HttpStatus.BAD_REQUEST);
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(userRepository.getReferenceById(userId));
        enrollment.setTutorial(tutorial);
        enrollmentRepository.save(enrollment);
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
}