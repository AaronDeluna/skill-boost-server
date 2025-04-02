package org.javaacademy.skillboostservice.repository;

import org.javaacademy.skillboostservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
