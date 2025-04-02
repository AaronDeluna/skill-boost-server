package org.javaacademy.skillboostservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaacademy.skillboostservice.dto.QuestionDtoRes;
import org.javaacademy.skillboostservice.dto.QuestionDtoRq;
import org.javaacademy.skillboostservice.entity.Answer;
import org.javaacademy.skillboostservice.entity.Question;
import org.javaacademy.skillboostservice.exception.IntegrationException;
import org.javaacademy.skillboostservice.repository.AnswerRepository;
import org.javaacademy.skillboostservice.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {
    private static final String PROMPT_ROLE = "system";
    private static final String AI_MODEL = "gpt-3.5-turbo";
    private static final Double AI_RESPONSE_TEMPERATURE = 0.5;
    private static final String PROMPT_MESSAGE = "Сгенерируй строго валидный JSON без лишних полей и комментариев. " +
            "JSON должен содержать одну тему с названием: \\\"%s\\\". " +
            "Добавь ровно 5 уникальных вопросов по этой теме на русском языке. " +
            "К каждому вопросу придумай ровно 3 варианта ответа на русском, содержащих осмысленную информацию. " +
            "Ответы не должны быть примитивными, а должны отражать ключевые аспекты и принципы темы. " +
            "Один из ответов должен быть правильным и иметь значение \\\"isCorrect\\\": true, остальные должны иметь \\\"isCorrect\\\": false. " +
            "Все вопросы должны быть разнообразными, с различной сложностью и формулировкой, и должны охватывать разные аспекты темы. " +
            "Используй только следующие поля: \\\"topic\\\", \\\"questions\\\", \\\"question\\\", \\\"answers\\\", \\\"text\\\", \\\"isCorrect\\\".";
    @Value("${integration-ai.api-key}")
    private String apiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionDtoRes create(QuestionDtoRq questionDtoRq) {
        OpenAiService service = new OpenAiService(apiKey);
        ChatMessage systemMessage = new ChatMessage(PROMPT_ROLE, String.format(PROMPT_MESSAGE, questionDtoRq.getTopic()));
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(AI_MODEL)
                .messages(List.of(systemMessage))
                .temperature(AI_RESPONSE_TEMPERATURE)
                .build();

        ChatCompletionResult response = service.createChatCompletion(request);
        String responseBody = response.getChoices().get(0).getMessage().getContent();

        try {
            QuestionDtoRes questionDtoRes = parseQuestions(responseBody, questionDtoRq);
            questionDtoRes.setChatId(questionDtoRq.getChatId());
            questionDtoRes.setTopic(questionDtoRq.getTopic());
            return questionDtoRes;
        } catch (JsonProcessingException e) {
            log.error("Ошибка парсинга json: {}", e.getMessage());
            throw new IntegrationException(String.format("Произошла ошибка: %s", e.getMessage()));
        }
    }

    protected QuestionDtoRes parseQuestions(String responseBody, QuestionDtoRq rq) throws JsonProcessingException {
        QuestionDtoRes questionDtoRes = new QuestionDtoRes();
        List<Question> questions = new ArrayList<>();

        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode questionArray = root.path("questions");

        if (questionArray.isMissingNode()) {
            throw new IllegalStateException("Не удалось найти questions в ответе OpenAI");
        }

        for (JsonNode qNode : questionArray) {
            Question question = processQuestionNode(qNode, rq);
            questions.add(question);
        }

        questionDtoRes.setQuestions(questions);
        return questionDtoRes;
    }

    private Question processQuestionNode(JsonNode qNode, QuestionDtoRq rq) {
        String questionText = qNode.get("question").asText();
        List<Answer> answers = new ArrayList<>();

        Question question = new Question(rq.getChatId(), rq.getTopic(), questionText);
        questionRepository.save(question);

        for (JsonNode ansNode : qNode.get("answers")) {
            Answer answer = parseAnswerContentByJsonNode(ansNode);
            answer.setQuestion(question);
            answers.add(answer);
        }

        Collections.shuffle(answers);

        answerRepository.saveAll(answers);
        question.setAnswers(answers);
        questionRepository.save(question);

        return question;
    }

    private Answer parseAnswerContentByJsonNode(JsonNode ansNode) {
        String text = ansNode.get("text").asText();
        boolean isCorrect = ansNode.get("isCorrect").asBoolean();
        return new Answer(text, isCorrect);
    }
}
