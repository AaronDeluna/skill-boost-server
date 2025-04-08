package org.javaacademy.skillboostservice.dto.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerDtoRq {
    private Long chatId;
    private Integer questionId;
    private Integer answerId;
}
