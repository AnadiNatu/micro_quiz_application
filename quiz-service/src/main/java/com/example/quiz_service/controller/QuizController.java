package com.example.quiz_service.controller;

import com.example.quiz_service.dto.applicationDTO.*;
import com.example.quiz_service.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin("*")
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody CreateQuizDto dto) {
        return ResponseEntity.ok(quizService.createQuiz(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuiz(id));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<QuizDTO> getQuizByTitle(@PathVariable String title) {
        return ResponseEntity.ok(quizService.getQuizByTitle(title));
    }

    @GetMapping("/creator/{userId}")
    public ResponseEntity<List<QuizDTO>> getByCreator(@PathVariable Long userId) {
        return ResponseEntity.ok(quizService.getQuizzesByCreator(userId));
    }

    @GetMapping("/participant/{userId}")
    public ResponseEntity<List<String>> getParticipantQuizzes(@PathVariable Long userId) {
        return ResponseEntity.ok(quizService.getQuizTitlesByParticipant(userId));
    }

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<String>> getQuestionTitles(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuestionTitlesOfQuiz(quizId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<QuestionResponseDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(quizService.getQuestionsByCategory(category));
    }

    @PostMapping("/{quizId}/start")
    public ResponseEntity<List<QuestionResponseDTO>> startQuiz(
            @PathVariable Long quizId,
            @RequestParam Long userId) {

        return ResponseEntity.ok(quizService.startQuiz(quizId, userId));
    }

    @PostMapping("/submit")
    public ResponseEntity<ResultDTO> submitQuiz(@RequestBody QuizTakenRequestDTO request) {
        return ResponseEntity.ok(quizService.submitQuiz(request));
    }
}