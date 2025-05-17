package com.example.technestbe.service.quizes;

import com.example.technestbe.dto.quizes.MCQRequest;
import com.example.technestbe.dto.quizes.MCQResponse;
import com.example.technestbe.dto.quizes.TutorialRequest;
import com.example.technestbe.dto.quizes.TutorialResponse;
import com.example.technestbe.mapper.QuizMapper;
import com.example.technestbe.model.quizes.Enrollment;
import com.example.technestbe.model.quizes.MCQ;
import com.example.technestbe.model.quizes.Tutorial;
import com.example.technestbe.repo.quizes.MCQRepository;
import com.example.technestbe.repo.quizes.TutorialRepository;
import com.example.technestbe.repo.quizes.EnrollmentRepository;
import com.example.technestbe.util.usermanagement.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {

    private final TutorialRepository tutorialRepository;
    private final MCQRepository mcqRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuizMapper quizMapper;

    public QuizService(TutorialRepository tutorialRepository, MCQRepository mcqRepository,
                       EnrollmentRepository enrollmentRepository, QuizMapper quizMapper) {
        this.tutorialRepository = tutorialRepository;
        this.mcqRepository = mcqRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.quizMapper = quizMapper;
    }

    public TutorialResponse createTutorial(TutorialRequest request) {
        Tutorial tutorial = new Tutorial();
        tutorial.setTitle(request.getTitle());
        tutorial.setDescription(request.getDescription());
        Tutorial savedTutorial = tutorialRepository.save(tutorial);
        return quizMapper.toTutorialResponse(savedTutorial);
    }

    public TutorialResponse updateTutorial(Long tutorialId, TutorialRequest request) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found", HttpStatus.NOT_FOUND));

        tutorial.setTitle(request.getTitle());
        if (request.getDescription() != null) {
            tutorial.setDescription(request.getDescription());
        }

        Tutorial updatedTutorial = tutorialRepository.save(tutorial);
        return quizMapper.toTutorialResponse(updatedTutorial);
    }

    @Transactional
    public void deleteTutorial(Long tutorialId) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found", HttpStatus.NOT_FOUND));
        List<Enrollment> enrollments = enrollmentRepository.findByTutorialId(tutorialId);
        if (!enrollments.isEmpty()) {
            throw new ApiException("Cannot delete tutorial with active enrollments", HttpStatus.BAD_REQUEST);
        }
        tutorialRepository.delete(tutorial);
    }

    public MCQResponse createMCQ(Long tutorialId, MCQRequest request) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found", HttpStatus.NOT_FOUND));

        MCQ mcq = new MCQ();
        mcq.setQuestion(request.getQuestion());
        mcq.setOptions(request.getOptions());
        int correctAnswerIndex = request.getOptions().indexOf(request.getCorrectAnswer());
        if (correctAnswerIndex == -1) {
            throw new ApiException("Correct answer must be one of the provided options", HttpStatus.BAD_REQUEST);
        }
        mcq.setCorrectAnswer(correctAnswerIndex);
        mcq.setTutorial(tutorial);

        tutorial.getMcqs().add(mcq);
        MCQ savedMCQ = mcqRepository.save(mcq);
        tutorialRepository.save(tutorial);

        return quizMapper.toMCQResponse(savedMCQ);
    }

    public MCQResponse updateMCQ(Long mcqId, MCQRequest request) {
        MCQ mcq = mcqRepository.findById(mcqId)
                .orElseThrow(() -> new ApiException("MCQ not found", HttpStatus.NOT_FOUND));

        mcq.setQuestion(request.getQuestion());
        mcq.setOptions(request.getOptions());
        int correctAnswerIndex = request.getOptions().indexOf(request.getCorrectAnswer());
        if (correctAnswerIndex == -1) {
            throw new ApiException("Correct answer must be one of the provided options", HttpStatus.BAD_REQUEST);
        }
        mcq.setCorrectAnswer(correctAnswerIndex);

        MCQ updatedMCQ = mcqRepository.save(mcq);
        return quizMapper.toMCQResponse(updatedMCQ);
    }

    public void deleteMCQ(Long mcqId) {
        MCQ mcq = mcqRepository.findById(mcqId)
                .orElseThrow(() -> new ApiException("MCQ not found", HttpStatus.NOT_FOUND));
        Tutorial tutorial = mcq.getTutorial();
        if (tutorial != null) {
            tutorial.getMcqs().remove(mcq);
            tutorialRepository.save(tutorial);
        }
        mcqRepository.delete(mcq);
    }

    public void deleteMCQFromTutorial(Long tutorialId, Long mcqId) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found", HttpStatus.NOT_FOUND));

        MCQ mcq = mcqRepository.findById(mcqId)
                .orElseThrow(() -> new ApiException("MCQ not found", HttpStatus.NOT_FOUND));

        if (!tutorial.getMcqs().contains(mcq)) {
            throw new ApiException("MCQ does not belong to this tutorial", HttpStatus.BAD_REQUEST);
        }

        tutorial.getMcqs().remove(mcq);
        tutorialRepository.save(tutorial);
        mcqRepository.delete(mcq);
    }

    public List<TutorialResponse> getAllTutorials() {
        List<Tutorial> tutorials = tutorialRepository.findAll();
        return quizMapper.toTutorialResponseList(tutorials);
    }

    public TutorialResponse getTutorial(Long tutorialId) {
        Tutorial tutorial = tutorialRepository.findById(tutorialId)
                .orElseThrow(() -> new ApiException("Tutorial not found", HttpStatus.NOT_FOUND));
        return quizMapper.toTutorialResponse(tutorial);
    }
}