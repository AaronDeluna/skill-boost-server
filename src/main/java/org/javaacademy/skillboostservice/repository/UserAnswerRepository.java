package org.javaacademy.skillboostservice.repository;

import org.javaacademy.skillboostservice.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
    @Query(value = """
            SELECT count(*)
            FROM user_answer
            JOIN question ON user_answer.question_id = question.id
            WHERE question.chat_id = :chatId
                   """, nativeQuery = true)
    Integer countAllByChatId(@Param("chatId") Long chatId);

    @Query(value = """
            SELECT Count(*)
            FROM user_answer
            JOIN answer ON user_answer.answer_id = answer.id
            JOIN question ON answer.question_id = question.id
            WHERE answer.is_correct = :isCorrect AND question.chat_id = :chatId
                   """, nativeQuery = true)
    Integer countAnswersByChatIdAndCorrectness(@Param("chatId") Long chatId,
                                               @Param("isCorrect") Boolean isCorrect);
}
