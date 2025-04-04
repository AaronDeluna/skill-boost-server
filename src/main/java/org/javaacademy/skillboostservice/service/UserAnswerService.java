package org.javaacademy.skillboostservice.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.skillboostservice.dto.UserAnswerDtoRq;
import org.javaacademy.skillboostservice.entity.Answer;
import org.javaacademy.skillboostservice.entity.Question;
import org.javaacademy.skillboostservice.entity.UserAnswer;
import org.javaacademy.skillboostservice.repository.AnswerRepository;
import org.javaacademy.skillboostservice.repository.QuestionRepository;
import org.javaacademy.skillboostservice.repository.UserAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserAnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;

    public boolean checkUserAnswer(UserAnswerDtoRq rq) {
        Question question = questionRepository.findById(rq.getQuestionId()).orElseThrow(
                () -> new RuntimeException("Вопрос с id: %s не найден".formatted(rq.getQuestionId()))
        );

        Answer userAnswerRq = question.getAnswers().get(rq.getAnswerId());

        Answer correctAnswer = question.getAnswers().stream()
                .filter(Answer::getIsCorrect)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Ошибка при поиске корректного ответа")
                );

        boolean userAnswerCorrect = Objects.equals(userAnswerRq.getId(), correctAnswer.getId());

        UserAnswer userAnswer = new UserAnswer(question, userAnswerRq, userAnswerCorrect);
        userAnswerRepository.save(userAnswer);


        return userAnswerCorrect;
    }
}
