package com.suplerteam.video_creator.service.image;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.suplerteam.video_creator.request.image.TextToImageRequest;
import com.suplerteam.video_creator.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Qualifier("gemini-ImageService")
public class GeminiImageServiceImpl implements ImageService {

    private final String MODEL = "/gemini-2.0-flash-preview-image-generation";
    private final String TYPE_OF_ENDPOINT = ":generateContent";
    private final int MAX_BUFFER = 10 * 1024 * 1024;

    @Value("${myapp.parameters.gemini-secret-key}")
    private String GEMINI_SECRET_KEY;

    @Value("${myapp.parameters.gemini-url}")
    private String GEMINI_URL;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public String generateAnImage(TextToImageRequest req) throws IOException {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode content = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();
            ObjectNode part = objectMapper.createObjectNode();
            String enhancedPrompt = "generate a high quality picture about: " + req.getText();
            part.put("text", enhancedPrompt);
            parts.add(part);
            content.set("parts", parts);
            contents.add(content);
            rootNode.set("contents", contents);
            
            ObjectNode generationConfig = objectMapper.createObjectNode();
            ArrayNode responseModalities = objectMapper.createArrayNode();
            responseModalities.add("TEXT").add("IMAGE");
            generationConfig.set("responseModalities", responseModalities);
            rootNode.set("generationConfig", generationConfig);
            
            WebClient webClient = WebClient.builder()
                    .baseUrl(GEMINI_URL)
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer -> configurer
                                    .defaultCodecs()
                                    .maxInMemorySize(MAX_BUFFER))
                            .build())
                    .build();
            
            String response = webClient
                    .post()
                    .uri(MODEL + TYPE_OF_ENDPOINT + "?key=" + GEMINI_SECRET_KEY)
                    .bodyValue(rootNode)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            // Parse response
            JsonNode jsonNode = objectMapper.readTree(response);
            String base64ImageData = null;
            
            // Navigate through candidates to find image data
            JsonNode candidates = jsonNode.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode contentNode = candidates.get(0).path("content");
                JsonNode partsNode = contentNode.path("parts");
                
                for (JsonNode partNode : partsNode) {
                    if (partNode.has("inlineData") && partNode.path("inlineData").has("data")) {
                        base64ImageData = partNode.path("inlineData").path("data").asText();
                        break;
                    }
                }
            }
            
            if (base64ImageData == null) {
                throw new RuntimeException("No image data found in the response");
            }
            
            byte[] imageBytes = Base64.getDecoder().decode(base64ImageData);
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(imageBytes));
            
            String uniqueId = UUID.randomUUID().toString();
            return cloudinaryService.uploadImage(resource, "gemini-" + uniqueId);
            
        } catch (Exception e) {
            throw new IOException("Failed to generate image with Gemini API: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> generateImages(List<TextToImageRequest> requests) throws InterruptedException, IOException {
        List<String> urls = new ArrayList<>();
        for (TextToImageRequest request : requests) {
            String newUrl = this.generateAnImage(request);
            urls.add(newUrl);
            Thread.sleep(1000);
        }
        return urls;
    }
}
