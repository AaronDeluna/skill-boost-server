package org.javaacademy.skillboostservice.repository;

import org.javaacademy.skillboostservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Optional<Question> findByName(String name);
    List<Question> findAllByTopic(String topic);
}
