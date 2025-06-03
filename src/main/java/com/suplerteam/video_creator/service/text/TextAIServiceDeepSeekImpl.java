package com.suplerteam.video_creator.service.text;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.request.text.GenerateTextRequest;
import com.suplerteam.video_creator.request.text.deepseek.DeepSeekApiBody;
import com.suplerteam.video_creator.request.text.gemini.GeminiApiBody;
import com.suplerteam.video_creator.response.shot_stack.create.ShotStackSuccessApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Qualifier("DeepSeek-TextService")
public class TextAIServiceDeepSeekImpl implements TextAIService{

    @Autowired
    @Qualifier("openRouter-webClient")
    private WebClient.Builder webClientBuilder;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String generateContent(GenerateTextRequest req) {
        try{
            DeepSeekApiBody body=DeepSeekApiBody.buildFromGenerateTextRequest(req);

            String response = webClientBuilder.build()
                    .post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
