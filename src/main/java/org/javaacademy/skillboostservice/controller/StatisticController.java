package org.javaacademy.skillboostservice.controller;

import lombok.RequiredArgsConstructor;
import org.javaacademy.skillboostservice.dto.statistic.UserStatisticDto;
import org.javaacademy.skillboostservice.service.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/{chatId}")
    public UserStatisticDto getUserStatisticByChatId(@PathVariable Long chatId) {
        return statisticService.getUserStatisticByChatId(chatId);
    }

}
