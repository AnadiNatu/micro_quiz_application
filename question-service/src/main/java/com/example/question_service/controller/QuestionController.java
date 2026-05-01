package com.example.question_service.controller;

import com.example.question_service.dto.applicationDTO.*;
import com.example.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class QuestionController {

    private final QuestionService questionService;

    // ADMIN / CURATOR
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','CURATOR')")
    public ResponseEntity<QuestionDto> createQuestion(@RequestBody CreateQuestionDto dto) {
        return ResponseEntity.ok(questionService.addQuestion(dto));
    }

    // AUTHENTICATED
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    // AUTHENTICATED
    @GetMapping("/category/{category}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuestionDto>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(questionService.getQuestionsByCategory(category));
    }

    // FEIGN (QUIZ SERVICE)
    @GetMapping("/generate")
    public ResponseEntity<List<Long>> generateQuiz(
            @RequestParam String category,
            @RequestParam Integer numQuestions) {

        return ResponseEntity.ok(
                questionService.getQuestionIdsForQuiz(category, numQuestions)
        );
    }

    // FEIGN
    @PostMapping("/fetch")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromIds(
            @RequestBody List<Long> ids) {

        return ResponseEntity.ok(questionService.getQuestionsFromIds(ids));
    }

    // AUTHENTICATED
    @PostMapping("/score")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Integer> calculateScore(
            @RequestBody List<ResponseDto> responses) {

        return ResponseEntity.ok(questionService.calculateScore(responses));
    }

    // AUTHENTICATED
    @PostMapping("/evaluate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ResponseEvaluationDto>> evaluate(
            @RequestBody List<ResponseDto> responses) {

        return ResponseEntity.ok(questionService.evaluateResponses(responses));
    }

//    DELETE
@DeleteMapping("/{id}")
@PostAuthorize("hasAnyAuthority('ADMIN','CURATOR')")
public ResponseEntity<QuestionDto> deleteQuestion(@PathVariable Long id) {
    return ResponseEntity.ok(questionService.deleteQuestion(id));
}
}

