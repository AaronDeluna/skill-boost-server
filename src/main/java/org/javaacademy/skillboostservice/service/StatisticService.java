package org.javaacademy.skillboostservice.service;

import lombok.RequiredArgsConstructor;
import org.javaacademy.skillboostservice.dto.statistic.UserStatisticDto;
import org.javaacademy.skillboostservice.repository.UserAnswerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final UserAnswerRepository userAnswerRepository;

    public UserStatisticDto getUserStatisticByChatId(Long chatId) {
        int totalAnswers = userAnswerRepository.countAllByChatId(chatId);
        int totalCorrectAnswers = userAnswerRepository.countAnswersByChatIdAndCorrectness(chatId, true);
        int totalNotCorrectAnswers = userAnswerRepository.countAnswersByChatIdAndCorrectness(chatId, false);
        return new UserStatisticDto(
                chatId,
                totalCorrectAnswers,
                totalNotCorrectAnswers,
                totalAnswers
        );
    }

}
