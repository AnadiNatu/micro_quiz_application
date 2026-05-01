package com.example.question_service.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseEvaluationDto {
    private String questionTitle;
    private String correctAnswer;
    private String participantAnswer;
}
