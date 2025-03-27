package org.javaacademy.skillboostservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javaacademy.skillboostservice.entity.Question;

import java.util.List;
import java.util.Queue;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDtoRes {
    private Long chatId;
    private String topic;
    private List<Question> questions;
}
