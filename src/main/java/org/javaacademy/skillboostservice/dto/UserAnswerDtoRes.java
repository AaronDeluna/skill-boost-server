package org.javaacademy.skillboostservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerDtoRes {
    private Long chatId;
    private Integer questionId;
    private Boolean isCorrect;
    private Integer correctAnswerId;
}
