package org.javaacademy.skillboostservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.javaacademy.skillboostservice.dto.QuestionDtoRes;
import org.javaacademy.skillboostservice.dto.QuestionDtoRq;
import org.javaacademy.skillboostservice.entity.Answer;
import org.javaacademy.skillboostservice.entity.Question;
import org.javaacademy.skillboostservice.exception.IntegrationException;
import org.javaacademy.skillboostservice.repository.AnswerRepository;
import org.javaacademy.skillboostservice.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Profile("test")
@RequiredArgsConstructor
public class QuestiontService {
    private static final String JSON_BODY = """
            {
                "messages": [{"role": "user", "content": "%s"}],
                "max_tokens": 500,
                "temperature": 0.7
            }
            """;
    private static final String PROMPT_MESSAGE = "Сгенерируй строго валидный JSON без лишних полей и комментариев. " +
            "JSON должен содержать одну тему с названием: \\\"%s\\\". " +
            "Добавь ровно 3 уникальных вопроса по этой теме на русском языке. " +
            "К каждому вопросу придумай ровно 3 варианта ответа на русском, где только один из них должен иметь перемешай ответы случайным образом, ответы не должны быть примитивными, а должны содержать осмысленную информацию. " +
            "\\\"isCorrect\\\": true, остальные \\\"isCorrect\\\": false. " +
            "Все вопросы и ответы должны отличаться между собой по смыслу и формулировке. " +
            "Используй только следующие поля: \\\"topic\\\", \\\"questions\\\", \\\"question\\\", \\\"answers\\\", \\\"text\\\", \\\"isCorrect\\\".";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final ObjectMapper objectMapper;
    private final OkHttpClient client;
    @Value("${integration-ai.api-key}")
    private String apiKey;
    @Value("${integration-ai.azure-openai-endpoint}")
    private String azureOpenaiEndpoint;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionDtoRes create(QuestionDtoRq questionDtoRq) {
        String jsonBody = String.format(JSON_BODY.formatted(PROMPT_MESSAGE.formatted(questionDtoRq.getTopic())));
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = buildRequest(body);

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                QuestionDtoRes questionDtoRes = parseQuestions(responseBody, questionDtoRq);
                questionDtoRes.setChatId(questionDtoRq.getChatId());
                questionDtoRes.setTopic(questionDtoRq.getTopic());
                return questionDtoRes;
            } else {
                throw new RuntimeException("Ошибка вызова OpenAI API: " + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            throw new IntegrationException(e);
        }
    }

    private Request buildRequest(RequestBody body) {
        return new Request.Builder()
                .url(azureOpenaiEndpoint)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    private QuestionDtoRes parseQuestions(String responseBody, QuestionDtoRq rq) throws JsonProcessingException {
        QuestionDtoRes questionDtoRes = new QuestionDtoRes();
        List<Question> questions = new ArrayList<>();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode messageContentNode = root
                .path("choices")
                .get(0)
                .path("message")
                .path("content");

        JsonNode topicNode = root.get("topic");
        String questionTopic = (topicNode != null && !topicNode.isMissingNode())
                ? topicNode.asText()
                : "%s".formatted(rq.getTopic());

        if (messageContentNode.isMissingNode()) {
            throw new IllegalStateException("Не удалось найти message.content в ответе OpenAI");
        }

        JsonNode innerJson = objectMapper.readTree(messageContentNode.asText());
        JsonNode questionArray = innerJson.path("questions");

        for (JsonNode qNode : questionArray) {
            String questionText = qNode.get("question").asText();
            List<Answer> answers = new ArrayList<>();

            log.info("RAW JSON: {}", questionText);

            Question question = new Question(rq.getChatId(), questionTopic, questionText);
            questionRepository.save(question);

            for (JsonNode ansNode : qNode.get("answers")) {
                String text = ansNode.get("text").asText();
                boolean isCorrect = ansNode.get("isCorrect").asBoolean();

                Answer answer = new Answer(text, isCorrect);
                answer.setQuestion(question);
                answers.add(answer);
            }

            answerRepository.saveAll(answers);

            question.setAnswers(answers);
            questionRepository.save(question);

            questions.add(question);
        }

        questionRepository.saveAll(questions);
        questionDtoRes.setQuestions(questions);
        return questionDtoRes;
    }
}
