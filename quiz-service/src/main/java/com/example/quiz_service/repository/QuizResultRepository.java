package com.example.quiz_service.repository;

import com.example.quiz_service.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult , Long> {
    List<QuizResult> findByQuizId(Long quizId);
    List<QuizResult> findByParticipantId(Long participantId);
    Optional<QuizResult> findByQuizIdAndParticipantId(Long quizId, Long participantId);
    boolean existsByQuizIdAndParticipantId(Long quizId, Long participantId);
}
