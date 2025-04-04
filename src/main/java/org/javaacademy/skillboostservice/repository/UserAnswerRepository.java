package org.javaacademy.skillboostservice.repository;

import org.javaacademy.skillboostservice.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
}
