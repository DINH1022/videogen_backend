package com.suplerteam.video_creator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${myapp.parameters.gemini-url}")
    private String geminiUrl;

    @Value("${myapp.parameters.togetherAI-url}")
    private String togetherAIUrl;

    @Value("${myapp.parameters.clipdrop-url}")
    private String clipdropUrl;

    @Bean(name = "gemini-webClient")
    public WebClient.Builder geminiWebClient() {
        return WebClient.builder()
                .baseUrl(geminiUrl);
    }

    @Bean(name = "togetherAI-webClient")
    public WebClient.Builder togetherAIWebClient() {
        return WebClient.builder()
                .baseUrl(togetherAIUrl);
    }

    @Bean(name = "clipdrop-webClient")
    public WebClient.Builder clipdropWebClient() {
        return WebClient.builder()
                .baseUrl(clipdropUrl);
    }
}
