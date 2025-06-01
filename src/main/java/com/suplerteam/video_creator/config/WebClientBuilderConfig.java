package com.suplerteam.video_creator.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientBuilderConfig {

    @Value("${myapp.parameters.groq-url}")
    private String GROQ_AI_BASE_URL;
    @Value("${myapp.parameters.groq-secret-key}")
    private String GROQ_AI_SECRET_KEY;

    @Value("${myapp.parameters.camb-url}")
    private String CAMB_AI_BASE_URL;
    @Value("${myapp.parameters.camb-secret-key}")
    private String CAMB_AI_SECRET_KEY;

    @Bean
    @Qualifier("groq-webClient")
    public WebClient.Builder groqWebClientBuilder(){

        return WebClient.builder()
                .baseUrl(GROQ_AI_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer "+GROQ_AI_SECRET_KEY);
    }

    @Bean
    @Qualifier("cambAI-webClient")
    public WebClient.Builder cambAIWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(CAMB_AI_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-api-key",CAMB_AI_SECRET_KEY);
    }
}
