package org.javaacademy.skillboostservice.repository;

import org.javaacademy.skillboostservice.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
}
