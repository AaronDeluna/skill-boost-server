package org.javaacademy.skillboostservice.repository;

import org.javaacademy.skillboostservice.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

}
