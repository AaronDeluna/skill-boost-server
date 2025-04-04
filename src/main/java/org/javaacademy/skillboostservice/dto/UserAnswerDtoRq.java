package org.javaacademy.skillboostservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerDtoRq {
    private Long chatId;
    private Integer questionId;
    private Integer answerId;
}
