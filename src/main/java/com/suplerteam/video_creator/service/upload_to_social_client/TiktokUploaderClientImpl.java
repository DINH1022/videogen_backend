package com.suplerteam.video_creator.service.upload_to_social_client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.suplerteam.video_creator.entity.TiktokUploads;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.SocialAccountConnectionRepository;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@Qualifier("tiktok-uploader-Service")
public class TiktokUploaderClientImpl implements VideoUploaderClient {
    private static final Logger logger = LoggerFactory.getLogger(TiktokUploaderClientImpl.class);
    private static final int CHUNK_SIZE = 5 * 1024 * 1024; // 5 MB per chunk

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocialAccountConnectionRepository socialAccountConnectionRepository;

    @Autowired
    @Qualifier("tiktok-webClient")
    private WebClient.Builder tiktokWebClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String uploadVideo(SocialVideoUploadRequest req) {
        String username = req.getUsername();
        logger.info("Getting TikTok access token for user: {}", username);

        String accessToken = getTiktokAccessToken(username);
        if (accessToken == null || accessToken.isEmpty()) {
            throw new ResourceNotFoundException("TikTok access token not found for user: " + username);
        }

        logger.info("Found TikTok access token for user: {}", username);
        logger.info("Starting TikTok video upload process for user: {}", username);

        try {
            // Step 1: Download video
            logger.info("Downloading video from URL: {}", req.getUrl());
            byte[] videoBytes = downloadVideo(req.getUrl());
            if (videoBytes.length == 0) {
                throw new IOException("Downloaded video is empty");
            }
            logger.info("Video downloaded successfully, size: {} bytes", videoBytes.length);

            // Step 2: Check creator info
            JsonNode creatorInfo = getCreatorInfo(accessToken);
            logger.info("Retrieved creator info: {}", creatorInfo);

            // Step 3: Initialize upload
            int videoSize = videoBytes.length;
            int chunkSize = CHUNK_SIZE;
            int totalChunks;
            int remaining = videoSize % chunkSize;

            if (remaining > 0 && remaining < 5 * 1024 * 1024) {
                totalChunks = videoSize / chunkSize; // gộp vào chunk cuối
            } else {
                totalChunks = (int) Math.ceil((double) videoSize / chunkSize);
            }

            JsonNode initResponse = initializeVideoUpload(accessToken, videoSize, chunkSize, totalChunks, req, creatorInfo);

            String publishId = initResponse.path("data").path("publish_id").asText();
            String uploadUrl = initResponse.path("data").path("upload_url").asText();
            logger.info("Upload initialized with publish ID: {} and URL: {}", publishId, uploadUrl);


            //int totalChunksForUpload = (int) Math.ceil((double) videoSize / chunkSize);
            // Step 4: Upload video (potentially in chunks)
            uploadVideoChunked(uploadUrl, videoBytes, chunkSize, totalChunks);

            logger.info("Video uploaded to TikTok successfully");

            // Step 5: Check upload status
            if (checkUploadStatus(publishId, accessToken)) {
                logger.info("Video publish completed successfully");
            } else {
                logger.warn("Video upload completed, but publishing status is pending");
            }


            return req.getTitle();



        } catch (Exception e) {
            logger.error("Error uploading video to TikTok: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload video to TikTok: " + e.getMessage(), e);
        }
    }


    private String getTiktokAccessToken(String username) {
        // Try to get token directly from SocialAccountConnection repository first
        try {
            return socialAccountConnectionRepository.findByUser_Username(username)
                    .map(connection -> connection.getTiktokToken())
                    .orElseGet(() -> {
                        // Fallback to getting through user if direct approach fails
                        User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
                        if (user.getSocialConnection() == null) {
                            throw new ResourceNotFoundException("Social connections not found for user: " + username);
                        }
                        return user.getSocialConnection().getTiktokToken();
                    });
        } catch (Exception e) {
            logger.error("Error retrieving TikTok token for user {}: {}", username, e.getMessage());
            throw new ResourceNotFoundException("Could not retrieve TikTok token for user: " + username);
        }
    }

    private byte[] downloadVideo(String videoUrl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(videoUrl))
                .timeout(Duration.ofMinutes(2))
                .build();

        logger.info("Sending request to download video from URL: {}", videoUrl);

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() >= 400) {
            throw new IOException("Failed to download video. Status code: " + response.statusCode());
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream is = response.body()) {
            byte[] data = new byte[16384];
            int bytesRead;
            while ((bytesRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
        }

        byte[] videoBytes = buffer.toByteArray();
        buffer.close();

        return videoBytes;
    }

    private JsonNode getCreatorInfo(String accessToken) {
        logger.info("Getting TikTok creator info");

        try {
            String response = tiktokWebClientBuilder.build()
                    .post()
                    .uri("https://open.tiktokapis.com/v2/post/publish/creator_info/query/")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .bodyValue("{}")
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                                            "Error response from TikTok creator info API: " +
                                                    clientResponse.statusCode() + " " + errorBody))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            logger.info("Creator info response: {}", response);

            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode error = jsonResponse.path("error");
            if (!error.path("code").asText("").equals("ok")) {
                String errorMessage = error.path("message").asText("Unknown error");
                logger.error("Error fetching creator info: {}", errorMessage);
                throw new RuntimeException("Failed to get creator info: " + errorMessage);
            }
            return jsonResponse.path("data");
        } catch (WebClientResponseException e) {
            logger.error("HTTP error when fetching creator info: {} - {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("TikTok API error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error parsing creator info response", e);
            throw new RuntimeException("Failed to parse creator info response", e);
        }
    }

    private JsonNode initializeVideoUpload(String accessToken, int videoSize, int chunkSize, int totalChunks, SocialVideoUploadRequest req, JsonNode creatorInfo){
     logger.info("Initializing TikTok video upload with size: {}", videoSize);

        try {


            logger.debug("Using chunk size: {}, total chunks: {}", chunkSize, totalChunks);

            // Create payload strictly according to TikTok API documentation
            ObjectNode payload = objectMapper.createObjectNode();

            // Post info
            ObjectNode postInfo = objectMapper.createObjectNode();
            postInfo.put("title", req.getTitle());
            postInfo.put("privacy_level", req.getPrivacyLevel() != null ? req.getPrivacyLevel() : "SELF_ONLY");
            postInfo.put("disable_duet", creatorInfo.path("duet_disabled").asBoolean(false));
            postInfo.put("disable_comment", creatorInfo.path("comment_disabled").asBoolean(false));
            postInfo.put("disable_stitch", creatorInfo.path("stitch_disabled").asBoolean(false));
            postInfo.put("video_cover_timestamp_ms", 1000);
            payload.set("post_info", postInfo);

            // Source info
            ObjectNode sourceInfo = objectMapper.createObjectNode();
            sourceInfo.put("source", "FILE_UPLOAD");
            sourceInfo.put("video_size", videoSize);
            sourceInfo.put("chunk_size", chunkSize);
            sourceInfo.put("total_chunk_count", totalChunks);
            payload.set("source_info", sourceInfo);

            logger.debug("Sending payload to TikTok init endpoint: {}", payload);

            String payloadJson = objectMapper.writeValueAsString(payload);

            // Use explicit headers to match TikTok API expectations
            String response = tiktokWebClientBuilder.build()
                    .post()
                    .uri("https://open.tiktokapis.com/v2/post/publish/video/init/")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .bodyValue(payloadJson)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                                            "Error response from TikTok init API: " +
                                                    clientResponse.statusCode() + " " + errorBody))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            logger.info("Init response received: {}", response);

            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode error = jsonResponse.path("error");

            if (!error.path("code").asText("").equals("ok")) {
                String errorMessage = error.path("message").asText("Unknown error");
                logger.error("Error initializing video upload: {}", errorMessage);
                throw new RuntimeException("Failed to initialize video upload: " + errorMessage);
            }

            // Validate the response contains required fields
            if (!jsonResponse.path("data").has("publish_id") || !jsonResponse.path("data").has("upload_url")) {
                logger.error("Invalid init response structure: {}", jsonResponse);
                throw new RuntimeException("TikTok API returned incomplete initialization data");
            }

            return jsonResponse;
        } catch (WebClientResponseException e) {
            logger.error("HTTP error when initializing upload: {} - {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("TikTok API error: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error initializing video upload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize video upload: " + e.getMessage(), e);
        }
    }

    private void uploadVideoChunked(String uploadUrl, byte[] videoData, int chunkSize, int totalChunks)
    throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60))
                .build();

        int videoSize = videoData.length;


        logger.info("Starting chunked upload: {} bytes in {} chunks to URL: {}", videoSize, totalChunks, uploadUrl);

        for (int chunkNumber = 0; chunkNumber < totalChunks; chunkNumber++) {
            int startByte = chunkNumber * chunkSize;
            int endByte;

            if (chunkNumber == totalChunks - 1) {
                endByte = videoSize - 1;
            } else {
                endByte = startByte + chunkSize - 1;
            }

            int currentChunkSize = endByte - startByte + 1;

            byte[] chunk = new byte[currentChunkSize];
            System.arraycopy(videoData, startByte, chunk, 0, currentChunkSize);

            // Remove Content-Length header - it will be set automatically by HttpClient
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .timeout(Duration.ofMinutes(5))
                    .header("Content-Type", "video/mp4")
                    .header("Content-Range", String.format("bytes %d-%d/%d", startByte, endByte, videoSize))
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(chunk))
                    .build();

            logger.info("Uploading chunk {} of {}, bytes {}-{}/{}",
                    chunkNumber + 1, totalChunks, startByte, endByte, videoSize);

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 201 && response.statusCode() != 202 && response.statusCode() != 206) {
                logger.error("Failed to upload chunk {} of {}. Status: {}, Body: {}",
                        chunkNumber + 1, totalChunks, response.statusCode(), response.body());
                throw new IOException("Failed to upload video chunk " + (chunkNumber + 1) + ". Status: " + response.statusCode() + ", Body: " + response.body());
            }

            logger.info("Uploaded chunk {} of {} successfully", chunkNumber + 1, totalChunks);

            // Add a small delay between chunks to avoid overwhelming the server
            if (chunkNumber < totalChunks - 1) {
                Thread.sleep(500);
            }
        }

        logger.info("All chunks uploaded successfully");
    }

    private boolean checkUploadStatus(String publishId, String accessToken) {
        logger.info("Checking upload status for publish ID: {}", publishId);

        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("publish_id", publishId);

            String payloadJson = objectMapper.writeValueAsString(payload);

            // Try up to 6 times with 10 second intervals
            for (int attempt = 0; attempt < 6; attempt++) {
                // Wait 10 seconds between checks
                if (attempt > 0) {
                    logger.info("Waiting 10 seconds before next status check (attempt {}/6)...", attempt + 1);
                    Thread.sleep(10000);
                }

                try {
                    String response = tiktokWebClientBuilder.build()
                            .post()
                            .uri("https://open.tiktokapis.com/v2/post/publish/status/fetch/")
                            .header("Authorization", "Bearer " + accessToken)
                            .header("Content-Type", "application/json; charset=UTF-8")
                            .bodyValue(payloadJson)
                            .retrieve()
                            .bodyToMono(String.class)
                            .timeout(Duration.ofSeconds(30))
                            .block();

                    logger.info("Status check response (attempt {}): {}", attempt + 1, response);

                    JsonNode jsonResponse = objectMapper.readTree(response);
                    JsonNode error = jsonResponse.path("error");

                    if (error.path("code").asText("").equals("ok")) {
                        String status = jsonResponse.path("data").path("status").asText("");
                        if ("PUBLISH_COMPLETE".equals(status)) {
                            logger.info("Video published successfully with status: {}", status);
                            return true;
                        } else if ("PUBLISH_FAILED".equals(status)) {
                            String failReason = jsonResponse.path("data").path("fail_reason").asText("Unknown reason");
                            logger.error("Publishing failed: {}", failReason);
                            return false;
                        }
                        logger.info("Current status: {} (waiting...)", status);
                    } else {
                        logger.warn("Error in status check: {}", error.path("message").asText());
                    }
                } catch (Exception e) {
                    logger.error("Error checking status (attempt {}): {}", attempt + 1, e.getMessage());
                    // Continue to next attempt
                }
            }

            logger.warn("Upload status check timed out after multiple attempts");
            return false;

        } catch (Exception e) {
            logger.error("Error checking upload status", e);
            return false;
        }
    }
}