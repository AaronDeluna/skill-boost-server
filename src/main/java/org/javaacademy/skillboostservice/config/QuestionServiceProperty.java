package org.javaacademy.skillboostservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "open-ai")
public class QuestionServiceProperty {
    private String apiKey;
    private String aiModel;
    private Double temperature;
    private String promptRole;
    private String promptMessage;
}
