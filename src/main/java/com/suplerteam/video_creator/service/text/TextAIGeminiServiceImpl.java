package com.suplerteam.video_creator.service.text;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.request.text.GenerateTextRequest;
import com.suplerteam.video_creator.request.text.gemini.GeminiApiBody;
import com.suplerteam.video_creator.util.PromptBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Qualifier("Gemini-TextService")
public class TextAIGeminiServiceImpl implements TextAIService{

    private final String MODEL="/gemini-2.0-flash";
    private final String TYPE_OF_ENDPOINT=":generateContent";

    @Value("${myapp.parameters.gemini-secret-key}")
    private String GEMINI_SECRET_KEY;

    @Autowired
    @Qualifier("gemini-webClient")
    private WebClient.Builder webClientBuilder;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String generateContent(GenerateTextRequest req) {
        try{
            GenerateTextRequest enhancedRequest = new GenerateTextRequest();
            enhancedRequest.setPrompt(PromptBuilder.buildPrompt(req));

            GeminiApiBody body=GeminiApiBody.buildFromGenerateTextRequest(enhancedRequest);
            String response = webClientBuilder.build()
                    .post()
                    .uri(MODEL+TYPE_OF_ENDPOINT+"?key="+GEMINI_SECRET_KEY)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode jsonNode = objectMapper.readTree(response);
            String genText = jsonNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
            return PromptBuilder.removeQuotationMarks(genText);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
