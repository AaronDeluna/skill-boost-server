package org.javaacademy.skillboostservice.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticDto {
    private Long chatId;
    private Integer totalCorrectAnswers;
    private Integer totalNotCorrectAnswers;
    private Integer totalAnswers;

}
