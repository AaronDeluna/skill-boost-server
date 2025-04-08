package org.javaacademy.skillboostservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QuestionServiceProperty.class)
public class QuestionServiceConfiguration {
}
