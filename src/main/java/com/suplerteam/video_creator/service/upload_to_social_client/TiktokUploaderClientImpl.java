package com.suplerteam.video_creator.service.upload_to_social_client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@Qualifier("tiktok-uploader-Service")
public class TiktokUploaderClientImpl implements VideoUploaderClient {
    private static final Logger log = LoggerFactory.getLogger(TiktokUploaderClientImpl.class);

    private final String INIT_UPLOAD_URL = "https://open.tiktokapis.com/v2/post/publish/inbox/video/init/";
    private final String PUBLISH_URL = "https://open.tiktokapis.com/v2/post/publish/inbox/video/";

    @Autowired
    private WebClient.Builder tiktokWebClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    // Add filter functions for detailed request/response logging
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status: {}", clientResponse.statusCode());
            return clientResponse.bodyToMono(String.class)
                    .doOnNext(body -> log.info("Response body: {}", body))
                    .map(body -> clientResponse);
        });
    }

    private String getAccessTokenByUsername(String username) {
        log.info("Getting TikTok access token for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        String refreshToken = user.getSocialConnection().getTiktokToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.error("No TikTok refresh token found for user: {}", username);
            throw new ResourceNotFoundException("TikTok connection not established for user: " + username);
        }

        log.info("Found TikTok refresh token for user: {}", username);
        // For now, using refresh token directly as access token for testing
        return refreshToken;
    }

    @Override
    public String uploadVideo(SocialVideoUploadRequest req) {
        try {
            String accessToken = getAccessTokenByUsername(req.getUsername());
            log.info("Starting TikTok video upload process for user: {}", req.getUsername());

            // Step 1: Download the video from URL
            log.info("Downloading video from URL: {}", req.getUrl());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest downloadRequest = HttpRequest.newBuilder()
                    .uri(URI.create(req.getUrl()))
                    .build();

            HttpResponse<byte[]> downloadResponse = client.send(downloadRequest, HttpResponse.BodyHandlers.ofByteArray());
            byte[] videoData = downloadResponse.body();
            log.info("Video downloaded successfully, size: {} bytes", videoData.length);

            // Step 2: Initialize upload with the TikTok API
            WebClient webClient = tiktokWebClientBuilder
                    .filter(logRequest())
                    .filter(logResponse())
                    .build();

            // Create correctly formatted request body for TikTok API
            Map<String, Object> postInfo = new HashMap<>();
            postInfo.put("title", req.getTitle() != null ? req.getTitle() : "");

            if (req.getDescription() != null && !req.getDescription().isEmpty()) {
                postInfo.put("description", req.getDescription());
            }

            postInfo.put("privacy_level", req.getPrivacyLevel() != null ? req.getPrivacyLevel() : "PUBLIC");
            postInfo.put("disable_comment", false);
            postInfo.put("disable_duet", false);
            postInfo.put("disable_stitch", false);

            Map<String, Object> initBody = new HashMap<>();
            initBody.put("post_info", postInfo);

            log.info("Initializing TikTok upload with endpoint: {}", INIT_UPLOAD_URL);
            log.info("Init request payload: {}", objectMapper.writeValueAsString(initBody));
            log.info("Using authorization token: Bearer {}", accessToken);

            String initResponse;
            try {
                initResponse = webClient
                        .post()
                        .uri(INIT_UPLOAD_URL)
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .bodyValue(initBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                log.info("TikTok init upload complete response: {}", initResponse);
            } catch (Exception e) {
                log.error("Error during init upload request", e);
                throw new RuntimeException("Failed during TikTok initialization: " + e.getMessage(), e);
            }

            JsonNode initJson = objectMapper.readTree(initResponse);

            // Detailed error checking
            if (initJson.has("error") && !initJson.path("error").isNull()) {
                String errorCode = initJson.path("error").path("code").asText();
                String errorMessage = initJson.path("error").path("message").asText();
                log.error("TikTok API error: {} - {}", errorCode, errorMessage);
                throw new RuntimeException("TikTok API error: " + errorCode + " - " + errorMessage);
            }

            // Extract upload parameters
            String uploadId = initJson.path("data").path("upload_id").asText();
            String uploadUrl = initJson.path("data").path("upload_url").asText();

            log.info("Received upload_id: {}", uploadId);
            log.info("Received upload_url: {}", uploadUrl);

            if (uploadId == null || uploadId.isEmpty() || uploadUrl == null || uploadUrl.isEmpty()) {
                log.error("Failed to get valid upload details from TikTok: {}", initResponse);
                throw new RuntimeException("Failed to get valid upload details from TikTok");
            }

            // Step 3: Upload video content directly to the provided upload URL
            log.info("Uploading video content to URL: {}", uploadUrl);
            HttpRequest uploadRequest = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .header("Content-Type", "application/octet-stream")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(videoData))
                    .build();

            HttpResponse<String> uploadResponse = client.send(uploadRequest, HttpResponse.BodyHandlers.ofString());
            log.info("Upload response status: {}", uploadResponse.statusCode());
            log.info("Upload response body: {}", uploadResponse.body());

            if (uploadResponse.statusCode() != 200) {
                log.error("Failed to upload video content: {} {}", uploadResponse.statusCode(), uploadResponse.body());
                throw new RuntimeException("Failed to upload video content: " + uploadResponse.body());
            }
            log.info("Video content upload successful");

            // Step 4: Publish the video with the new endpoint
            Map<String, Object> publishBody = new HashMap<>();
            publishBody.put("upload_id", uploadId);

            log.info("Publishing video with upload_id: {}", uploadId);
            log.info("Publish request payload: {}", objectMapper.writeValueAsString(publishBody));

            String publishResponse;
            try {
                publishResponse = webClient
                        .post()
                        .uri(PUBLISH_URL)
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .bodyValue(publishBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                log.info("TikTok publish complete response: {}", publishResponse);
            } catch (Exception e) {
                log.error("Error during video publish request", e);
                throw new RuntimeException("Failed during TikTok publish: " + e.getMessage(), e);
            }

            JsonNode publishJson = objectMapper.readTree(publishResponse);

            // Check for error in publish response
            if (publishJson.has("error") && !publishJson.path("error").isNull()) {
                String errorCode = publishJson.path("error").path("code").asText();
                String errorMessage = publishJson.path("error").path("message").asText();
                log.error("TikTok API publish error: {} - {}", errorCode, errorMessage);
                throw new RuntimeException("TikTok API publish error: " + errorCode + " - " + errorMessage);
            }

            String videoId = publishJson.path("data").path("video_id").asText();
            log.info("Successfully uploaded video to TikTok with ID: {}", videoId);
            return videoId;

        } catch (IOException | InterruptedException e) {
            log.error("Failed to upload video to TikTok", e);
            throw new RuntimeException("Failed to upload video to TikTok: " + e.getMessage(), e);
        }
    }
}