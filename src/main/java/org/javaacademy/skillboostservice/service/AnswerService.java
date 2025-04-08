package org.javaacademy.skillboostservice.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.skillboostservice.dto.answer.UserAnswerDtoRes;
import org.javaacademy.skillboostservice.dto.answer.UserAnswerDtoRq;
import org.javaacademy.skillboostservice.entity.Answer;
import org.javaacademy.skillboostservice.entity.Question;
import org.javaacademy.skillboostservice.entity.UserAnswer;
import org.javaacademy.skillboostservice.repository.QuestionRepository;
import org.javaacademy.skillboostservice.repository.UserAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;

    public UserAnswerDtoRes check(UserAnswerDtoRq rq) {
        Question question = questionRepository.findById(rq.getQuestionId()).orElseThrow(
                () -> new RuntimeException("Вопрос с id: %s не найден".formatted(rq.getQuestionId()))
        );

        Answer userAnswerRq = question.getUserAnswerById(rq.getAnswerId());
        Answer correctAnswer = question.getCorrectAnswer();
        boolean userAnswerCorrect = checkUserAnswer(userAnswerRq, correctAnswer);

        UserAnswer userAnswer = new UserAnswer(question, userAnswerRq, userAnswerCorrect);
        userAnswerRepository.save(userAnswer);

        return UserAnswerDtoRes.builder()
                .chatId(question.getChatId())
                .questionId(question.getId())
                .isCorrect(userAnswerCorrect)
                .correctAnswerId(correctAnswer.getId())
                .build();
    }

    private boolean checkUserAnswer(Answer userAnswer, Answer correctAnswer) {
        return Objects.equals(userAnswer.getId(), correctAnswer.getId());
    }

}
