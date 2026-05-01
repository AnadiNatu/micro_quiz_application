package com.example.question_service.mapper;

import com.example.question_service.dto.applicationDTO.*;
import com.example.question_service.entity.Questions;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {

    // ================= ENTITY → DTO =================

    public static QuestionDto toDTO(Questions question) {
        if (question == null) return null;

        return QuestionDto.builder()
                .id(question.getId())
                .questionTitle(question.getQuestionTitle())
                .category(question.getCategory())
                .difficultyLevel(question.getDifficultyLevel())
                .rightAnswer(question.getRightAnswer())
                .option1(question.getOption1())
                .option2(question.getOption2())
                .option3(question.getOption3())
                .option4(question.getOption4())
                .build();
    }

    public static List<QuestionDto> toDTOList(List<Questions> questions) {
        return questions.stream()
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ================= DTO → ENTITY =================

    public static Questions toEntity(CreateQuestionDto dto) {
        if (dto == null) return null;

        return Questions.builder()
                .questionTitle(dto.getQuestionTitle())
                .category(dto.getCategory())
                .difficultyLevel(dto.getDifficultyLevel())
                .rightAnswer(dto.getRightAnswer())
                .option1(dto.getOption1())
                .option2(dto.getOption2())
                .option3(dto.getOption3())
                .option4(dto.getOption4())
                .build();
    }

    // ================= WRAPPER =================

    public static QuestionWrapper toWrapper(Questions question) {
        if (question == null) return null;

        return new QuestionWrapper(
                question.getQuestionTitle(),
                question.getOption1(),
                question.getOption2(),
                question.getOption3(),
                question.getOption4()
        );
    }

    public static List<QuestionWrapper> toWrapperList(List<Questions> questions) {
        return questions.stream()
                .map(QuestionMapper::toWrapper)
                .collect(Collectors.toList());
    }

    // ================= EVALUATION =================

    public static ResponseEvaluationDto evaluateResponse(Questions question, ResponseDto response) {

        return new ResponseEvaluationDto(
                question.getQuestionTitle(),
                question.getRightAnswer(),
                response.getSelectedAnswer()
        );
    }

    public static List<ResponseEvaluationDto> evaluateResponses(
            List<Questions> questions,
            List<ResponseDto> responses
    ) {

        return questions.stream().map(question -> {

            String selectedAnswer = responses.stream()
                    .filter(r -> r.getQuestionTitle().equals(question.getQuestionTitle()))
                    .map(ResponseDto::getSelectedAnswer)
                    .findFirst()
                    .orElse(null);

            return new ResponseEvaluationDto(
                    question.getQuestionTitle(),
                    question.getRightAnswer(),
                    selectedAnswer
            );

        }).collect(Collectors.toList());
    }
}
