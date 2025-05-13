package com.resumeanalyzer.resumeanalyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    @Bean
   public WebClient openAIClient() {
        return WebClient.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent");
    }
}
