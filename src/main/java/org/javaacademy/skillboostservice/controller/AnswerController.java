package org.javaacademy.skillboostservice.controller;

import lombok.RequiredArgsConstructor;
import org.javaacademy.skillboostservice.dto.answer.UserAnswerDtoRes;
import org.javaacademy.skillboostservice.dto.answer.UserAnswerDtoRq;
import org.javaacademy.skillboostservice.service.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAnswerDtoRes check(@RequestBody UserAnswerDtoRq userAnswerDtoRq) {
        return answerService.check(userAnswerDtoRq);
    }
}
