package org.javaacademy.skillboostservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    @JsonIgnore
    private String topic;

    @Column(nullable = false)
    private String name;

    @OneToMany
    @JoinColumn(name = "question_id")
    private List<Answer> answers;

    public Question(Long chatId, String topic, String name) {
        this.chatId = chatId;
        this.topic = topic;
        this.name = name;
    }
}
