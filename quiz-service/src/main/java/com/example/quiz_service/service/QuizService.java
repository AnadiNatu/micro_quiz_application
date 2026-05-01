package com.example.quiz_service.service;

import com.example.quiz_service.dto.applicationDTO.*;
import com.example.quiz_service.entity.Quiz;
import com.example.quiz_service.feign.QuestionClient;
import com.example.quiz_service.feign.UserClient;
import com.example.quiz_service.mapper.QuizMapper;
import com.example.quiz_service.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionClient questionClient;
    private final UserClient userClient;

    // ================= CREATE QUIZ =================
    public QuizDTO createQuiz(CreateQuizDto dto) {

        List<Long> questionIds = questionClient.generateQuestions(
                dto.getCategory(),
                dto.getNumberOfQuestions()
        );

        if (questionIds == null || questionIds.isEmpty()) {
            throw new RuntimeException("No questions available");
        }

        Quiz quiz = QuizMapper.toEntity(dto, questionIds);
        Quiz saved = quizRepository.save(quiz);

        UserResponseDTO creator = userClient.getUserById(dto.getCreatedByUserId());

        return QuizMapper.toDTO(saved, creator, null);
    }

    // ================= GET QUIZ BY ID =================
    public QuizDTO getQuiz(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        UserResponseDTO creator = userClient.getUserById(quiz.getCreatedByUserId());

        List<UserResponseDTO> participants = quiz.getParticipantIds() != null
                ? quiz.getParticipantIds().stream()
                .map(userClient::getUserById)
                .toList()
                : null;

        return QuizMapper.toDTO(quiz, creator, participants);
    }

    // ================= GET QUIZ BY TITLE (OLD FEATURE) =================
    public QuizDTO getQuizByTitle(String title) {

        Quiz quiz = quizRepository.findByTitleIgnoreCase(title)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        return getQuiz(quiz.getId());
    }

    // ================= GET ALL QUIZ BY CREATOR =================
    public List<QuizDTO> getQuizzesByCreator(Long userId) {

        return quizRepository.findByCreatedByUserId(userId)
                .stream()
                .map(q -> {
                    UserResponseDTO creator = userClient.getUserById(userId);
                    return QuizMapper.toDTO(q, creator, null);
                })
                .toList();
    }

    // ================= GET ALL QUIZ TITLES BY PARTICIPANT =================
    public List<String> getQuizTitlesByParticipant(Long userId) {

        return quizRepository.findByParticipantId(userId)
                .stream()
                .map(Quiz::getTitle)
                .toList();
    }

    // ================= GET QUESTIONS OF QUIZ =================
    public List<String> getQuestionTitlesOfQuiz(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<QuestionResponseDTO> questions =
                questionClient.getQuestionsByIds(quiz.getQuestionIds());

        return questions.stream()
                .map(QuestionResponseDTO::getQuestionTitle)
                .toList();
    }

    // ================= GET QUESTIONS BY CATEGORY =================
    public List<QuestionResponseDTO> getQuestionsByCategory(String category) {

        // delegate to question-service
        return questionClient.getQuestionsByCategory(category);
    }

    // ================= START QUIZ (PARTICIPATION) =================
    public List<QuestionResponseDTO> startQuiz(Long quizId, Long userId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (quiz.getParticipantIds() != null &&
                quiz.getParticipantIds().contains(userId)) {
            throw new RuntimeException("User already attempted quiz");
        }

        if (quiz.getParticipantIds() == null) {
            quiz.setParticipantIds(new ArrayList<>());
        }

        quiz.getParticipantIds().add(userId);
        quizRepository.save(quiz);

        return questionClient.getQuestionsByIds(quiz.getQuestionIds());
    }

    // ================= SUBMIT QUIZ =================
    public ResultDTO submitQuiz(QuizTakenRequestDTO request) {

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<QuestionResponseDTO> questions =
                questionClient.getQuestionsByIds(quiz.getQuestionIds());

        int correct = 0;

        for (ResponseDTO response : request.getResponses()) {

            QuestionResponseDTO q = questions.stream()
                    .filter(x -> x.getId().equals(response.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            if (response.getSelectedAnswer().equalsIgnoreCase(q.getRightAnswer())) {
                correct++;
            }
        }

        int total = quiz.getQuestionIds().size();
        int incorrect = total - correct;

        UserResponseDTO user = userClient.getUserById(request.getUserId());

        return ResultDTO.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .userId(user.getId())
                .username(user.getUsername())
                .totalQuestions(total)
                .correctAnswers(correct)
                .incorrectAnswers(incorrect)
                .percentage((correct * 100.0) / total)
                .build();
    }

    // ================= DELETE QUIZ =================
    public QuizDTO deleteQuiz(Long id) {

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        QuizDTO dto = QuizMapper.toDTO(quiz, null, null);

        quizRepository.delete(quiz);

        return dto; // 🔥 needed for @PostAuthorize
    }
}
