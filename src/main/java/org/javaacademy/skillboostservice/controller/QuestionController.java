package org.javaacademy.skillboostservice.controller;

import lombok.RequiredArgsConstructor;
import org.javaacademy.skillboostservice.dto.QuestionDtoRes;
import org.javaacademy.skillboostservice.dto.QuestionDtoRq;
import org.javaacademy.skillboostservice.service.QuestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public QuestionDtoRes create(@RequestBody QuestionDtoRq questionDtoRq) {
        return questionService.create(questionDtoRq);
    }
}
