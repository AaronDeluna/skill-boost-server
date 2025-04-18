package org.javaacademy.skillboostservice.controller;

import lombok.RequiredArgsConstructor;
import org.javaacademy.skillboostservice.dto.question.QuestionDtoRes;
import org.javaacademy.skillboostservice.dto.question.QuestionDtoRq;
import org.javaacademy.skillboostservice.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionDtoRes create(@RequestBody QuestionDtoRq questionDtoRq) {
        return questionService.create(questionDtoRq);
    }
}
