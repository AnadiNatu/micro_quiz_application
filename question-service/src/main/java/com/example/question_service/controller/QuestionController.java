package com.example.question_service.controller;

import com.example.question_service.dto.applicationDTO.*;
import com.example.question_service.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class QuestionController {

    private final QuestionService questionService;

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<QuestionDto> createQuestion(@RequestBody CreateQuestionDto dto) {
        return ResponseEntity.ok(questionService.addQuestion(dto));
    }

    // ================= GET =================

    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<QuestionDto>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(questionService.getQuestionsByCategory(category));
    }

    // ================= QUIZ INTEGRATION =================

    @GetMapping("/generate")
    public ResponseEntity<List<Long>> generateQuiz(
            @RequestParam String category,
            @RequestParam Integer numQuestions) {

        return ResponseEntity.ok(
                questionService.getQuestionIdsForQuiz(category, numQuestions)
        );
    }

    @PostMapping("/fetch")
    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromIds(
            @RequestBody List<Long> ids) {

        return ResponseEntity.ok(questionService.getQuestionsFromIds(ids));
    }

    // ================= EVALUATION =================

    @PostMapping("/score")
    public ResponseEntity<Integer> calculateScore(
            @RequestBody List<ResponseDto> responses) {

        return ResponseEntity.ok(questionService.calculateScore(responses));
    }

    @PostMapping("/evaluate")
    public ResponseEntity<List<ResponseEvaluationDto>> evaluate(
            @RequestBody List<ResponseDto> responses) {

        return ResponseEntity.ok(questionService.evaluateResponses(responses));
    }
}

