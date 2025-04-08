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
import org.javaacademy.skillboostservice.config.QuestionServiceProperty;
import org.javaacademy.skillboostservice.dto.question.QuestionDtoRes;
import org.javaacademy.skillboostservice.dto.question.QuestionDtoRq;
import org.javaacademy.skillboostservice.entity.Answer;
import org.javaacademy.skillboostservice.entity.Question;
import org.javaacademy.skillboostservice.exception.IntegrationException;
import org.javaacademy.skillboostservice.repository.AnswerRepository;
import org.javaacademy.skillboostservice.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionServiceProperty questionProperty;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuestionDtoRes create(QuestionDtoRq questionDtoRq) {
        OpenAiService service = new OpenAiService(questionProperty.getApiKey());
        ChatMessage systemMessage = new ChatMessage(
                questionProperty.getPromptRole(),
                String.format(questionProperty.getPromptMessage(), questionDtoRq.getTopic())
        );
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(questionProperty.getAiModel())
                .messages(List.of(systemMessage))
                .temperature(questionProperty.getTemperature())
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
