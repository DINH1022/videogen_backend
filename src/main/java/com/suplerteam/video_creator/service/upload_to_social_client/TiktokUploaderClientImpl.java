package com.suplerteam.video_creator.service.upload_to_social_client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Qualifier("tiktok-uploader-Service")
public class TiktokUploaderClientImpl implements VideoUploaderClient {

    @Value("${myapp.parameters.tiktok-client-key}")
    private String TIKTOK_CLIENT_KEY;

    @Value("${myapp.parameters.tiktok-client-secret}")
    private String TIKTOK_CLIENT_SECRET;

    private final String TIKTOK_API_BASE_URL = "https://open.tiktokapis.com/v2/";
    private final String TOKEN_REFRESH_URL = TIKTOK_API_BASE_URL + "oauth/token/";
    private final String INIT_UPLOAD_URL = TIKTOK_API_BASE_URL + "video/upload/";
    private final String PUBLISH_URL = TIKTOK_API_BASE_URL + "video/publish/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("tiktok-webClient")
    private WebClient.Builder tiktokWebClientBuilder;

    private String refreshAccessToken(String refreshToken) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("client_key", TIKTOK_CLIENT_KEY);
            formData.add("client_secret", TIKTOK_CLIENT_SECRET);
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_REFRESH_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                return jsonResponse.path("data").path("access_token").asText();
            } else {
                throw new RuntimeException("Failed to refresh token: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error refreshing TikTok access token: " + e.getMessage(), e);
        }
    }

    private String getAccessTokenByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        String refreshToken = user.getSocialConnection().getTiktokToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ResourceNotFoundException("TikTok refresh token not found for user: " + username);
        }

        return refreshAccessToken(refreshToken);
    }

    @Override
    public String uploadVideo(SocialVideoUploadRequest req) {
        try {
            String accessToken = getAccessTokenByUsername(req.getUsername());

            // Step 1: Download the video from URL
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest downloadRequest = HttpRequest.newBuilder()
                    .uri(URI.create(req.getUrl()))
                    .build();
            HttpResponse<byte[]> downloadResponse = client.send(downloadRequest, HttpResponse.BodyHandlers.ofByteArray());
            byte[] videoData = downloadResponse.body();

            // Step 2: Initialize upload
            WebClient webClient = tiktokWebClientBuilder.build();

            // Create initialization request
            Map<String, Object> initBody = new HashMap<>();
            initBody.put("source_info", Map.of("source", "FILE_UPLOAD"));

            String initResponse = webClient
                    .post()
                    .uri(INIT_UPLOAD_URL)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(initBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode initJson = objectMapper.readTree(initResponse);
            String uploadId = initJson.path("data").path("upload_id").asText();
            String uploadUrl = initJson.path("data").path("upload_url").asText();

            // Step 3: Upload video content
            HttpRequest uploadRequest = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Content-Type", "application/octet-stream")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(videoData))
                    .build();

            HttpResponse<String> uploadResponse = client.send(uploadRequest, HttpResponse.BodyHandlers.ofString());

            if (uploadResponse.statusCode() != 200) {
                throw new RuntimeException("Failed to upload video content: " + uploadResponse.body());
            }

            // Step 4: Publish the video
            Map<String, Object> publishBody = new HashMap<>();
            publishBody.put("upload_id", uploadId);
            publishBody.put("title", req.getTitle());
            publishBody.put("description", req.getDescription() != null ? req.getDescription() : "");
            publishBody.put("privacy_level", req.getPrivacyLevel() != null ? req.getPrivacyLevel() : "PUBLIC");
            publishBody.put("disable_comment", false);
            publishBody.put("disable_duet", false);
            publishBody.put("disable_stitch", false);

            String publishResponse = webClient
                    .post()
                    .uri(PUBLISH_URL)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(publishBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode publishJson = objectMapper.readTree(publishResponse);
            return publishJson.path("data").path("video_id").asText();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to upload video to TikTok: " + e.getMessage(), e);
        }
    }
}