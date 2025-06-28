package com.suplerteam.video_creator.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
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

    @Value("${myapp.parameters.togetherAI-url}")
    private String TOGETHER_AI_BASE_URL;
    @Value("${myapp.parameters.togetherAI-secret-key}")
    private String TOGETHER_AI_SECRET_KEY;

    @Value("${myapp.parameters.clipdrop-url}")
    private String CLIP_DROP_BASE_URL;
    @Value("${myapp.parameters.clipdrop-secret-key}")
    private String CLIP_DROP_SECRET_KEY;

    @Value("${myapp.parameters.shot-stack-url}")
    private String SHOT_STACK_BASE_URL;
    @Value("${myapp.parameters.shot-stack-secret-key}")
    private String SHOT_STACK_SECRET_KEY;

    @Value("${myapp.parameters.open-router-url}")
    private String OPEN_ROUTER_BASE_URL;
    @Value("${myapp.parameters.open-router-secret-key}")
    private String OPEN_ROUTER_SECRET_KEY;

    @Value("${myapp.parameters.gemini-url}")
    private String GEMINI_BASE_URL;


    @Bean
    @Qualifier("groq-webClient")
    public WebClient.Builder groqWebClientBuilder(){

        return WebClient.builder()
                .baseUrl(GROQ_AI_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer "+GROQ_AI_SECRET_KEY);
    }

//    @Bean
//    @Qualifier("cambAI-webClient")
//    public WebClient.Builder cambAIWebClientBuilder(){
//        return WebClient.builder()
//                .baseUrl(CAMB_AI_BASE_URL)
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader("x-api-key",CAMB_AI_SECRET_KEY);
//    }

    @Bean
    @Qualifier("cambAI-webClient")
    public WebClient.Builder cambAIWebClientBuilder() {
        return WebClient.builder()
                .baseUrl(CAMB_AI_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-api-key", CAMB_AI_SECRET_KEY)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(20 * 1024 * 1024))
                        .build());
    }


    @Bean
    @Qualifier("togetherAI-webClient")
    public WebClient.Builder togetherAIWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(TOGETHER_AI_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer "+TOGETHER_AI_SECRET_KEY);
    }

    @Bean
    @Qualifier("clipdrop-webClient")
    public WebClient.Builder clipDropWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(CLIP_DROP_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.ALL_VALUE)
                .defaultHeader("X-API-KEY",CLIP_DROP_SECRET_KEY);
    }

    @Bean
    @Qualifier("shotStack-webClient")
    public WebClient.Builder shotStackWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(SHOT_STACK_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.ALL_VALUE)
                .defaultHeader("X-API-KEY",SHOT_STACK_SECRET_KEY);
    }

    @Bean
    @Qualifier("openRouter-webClient")
    public WebClient.Builder openRouterWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(OPEN_ROUTER_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION,"Bearer "+OPEN_ROUTER_SECRET_KEY);
    }

    @Bean
    @Qualifier("gemini-webClient")
    public WebClient.Builder geminiWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(GEMINI_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    @Qualifier("tiktok-webClient")
    public WebClient.Builder tiktokWebClientBuilder(){
        return WebClient.builder()
                .baseUrl("https://open.tiktokapis.com/v2/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
}
