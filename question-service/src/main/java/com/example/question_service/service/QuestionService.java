package com.example.question_service.service;

import com.example.question_service.dto.applicationDTO.*;
import com.example.question_service.dto.quizDTO.QuestionResponseDTO;
import com.example.question_service.entity.Questions;
import com.example.question_service.mapper.QuestionMapper;
import com.example.question_service.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    // ================= CREATE =================

    public QuestionDto addQuestion(CreateQuestionDto dto) {

        Questions question = QuestionMapper.toEntity(dto);
        Questions saved = questionRepository.save(question);

        return QuestionMapper.toDTO(saved);
    }

    // ================= GET ALL =================

    public List<QuestionDto> getAllQuestions() {
        return QuestionMapper.toDTOList(questionRepository.findAll());
    }

    // ================= FILTER =================

    public List<QuestionDto> getQuestionsByCategory(String category) {
        return QuestionMapper.toDTOList(
                questionRepository.findByCategoryIgnoreCase(category)
        );
    }

    // ================= QUIZ SUPPORT =================

    public List<Long> getQuestionIdsForQuiz(String category, Integer numQuestions) {
        return questionRepository.findRandomQuestionsByCategory(category, numQuestions);
    }

    public List<QuestionWrapper> getQuestionsFromIds(List<Long> questionIds) {

        List<Questions> questions = questionRepository.findAllById(questionIds);

        return QuestionMapper.toWrapperList(questions);
    }

    // ================= EVALUATION =================

    public Integer calculateScore(List<ResponseDto> responses) {

        int score = 0;

        for (ResponseDto response : responses) {

            Questions question = questionRepository
                    .findByQuestionTitle(response.getQuestionTitle())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            if (response.getSelectedAnswer().equalsIgnoreCase(question.getRightAnswer())) {
                score++;
            }
        }

        return score;
    }

    public List<ResponseEvaluationDto> evaluateResponses(List<ResponseDto> responses) {

        List<Questions> questions = responses.stream()
                .map(r -> questionRepository
                        .findByQuestionTitle(r.getQuestionTitle())
                        .orElseThrow(() -> new RuntimeException("Question not found")))
                .toList();

        return QuestionMapper.evaluateResponses(questions, responses);
    }

//    Feign Functionality
public List<QuestionResponseDTO> getQuestionsByIds(List<Long> ids) {

    List<Questions> questions = questionRepository.findAllById(ids);

    return questions.stream()
            .map(q -> QuestionResponseDTO.builder()
                    .id(q.getId())
                    .questionTitle(q.getQuestionTitle())
                    .option1(q.getOption1())
                    .option2(q.getOption2())
                    .option3(q.getOption3())
                    .option4(q.getOption4())
                    .rightAnswer(q.getRightAnswer())
                    .build())
            .toList();
}

    public List<QuestionResponseDTO> getQuestionsByCategoryFeign(String category) {

        return questionRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(q -> QuestionResponseDTO.builder()
                        .id(q.getId())
                        .questionTitle(q.getQuestionTitle())
                        .option1(q.getOption1())
                        .option2(q.getOption2())
                        .option3(q.getOption3())
                        .option4(q.getOption4())
                        .rightAnswer(q.getRightAnswer())
                        .build())
                .toList();
    }
}
