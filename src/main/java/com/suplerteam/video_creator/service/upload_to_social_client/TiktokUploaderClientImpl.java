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
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
@Qualifier("tiktok-uploader-Service")
public class TiktokUploaderClientImpl implements VideoUploaderClient {
    private static final Logger logger = LoggerFactory.getLogger(TiktokUploaderClientImpl.class);

    @Autowired
    private UserRepository userRepository;

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
            logger.info("Video downloaded successfully, size: {} bytes", videoBytes.length);

            // Step 2: Init direct upload
            JsonNode initResponse = initializeDirectUpload(accessToken, videoBytes.length);
            String publishId = initResponse.path("data").path("publish_id").asText();
            String uploadUrl = initResponse.path("data").path("upload_url").asText();
            logger.info("Upload initialized with publish ID: {}", publishId);

            // Step 3: Upload video
            uploadVideoToUrl(uploadUrl, videoBytes);
            logger.info("Video uploaded to TikTok successfully");

            return "Video uploaded to TikTok successfully";

        } catch (Exception e) {
            logger.error("Error uploading video to TikTok: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload video to TikTok: " + e.getMessage(), e);
        }
    }


    private String getTiktokAccessToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return user.getSocialConnection().getTiktokToken();
    }

    private byte[] downloadVideo(String videoUrl) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(videoUrl))
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        InputStream is = response.body();
        byte[] data = new byte[16384];
        int bytesRead;
        while ((bytesRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        is.close();

        return buffer.toByteArray();
    }

    private JsonNode initializeDirectUpload(String accessToken, int videoSize) {
        logger.info("Initializing TikTok direct upload");

        String payload = String.format("{" +
                "\"source_info\": {" +
                "\"source\": \"FILE_UPLOAD\"," +
                "\"video_size\": %d," +
                "\"chunk_size\": %d," +
                "\"total_chunk_count\": 1" +
                "}}", videoSize, videoSize);

        String response = tiktokWebClientBuilder.build()
                .post()
                .uri("https://open.tiktokapis.com/v2/post/publish/inbox/video/init/")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        logger.info("Init response: {}", response);

        try {
            return objectMapper.readTree(response);
        } catch (Exception e) {
            logger.error("Error parsing init response", e);
            throw new RuntimeException("Failed to parse init response", e);
        }
    }

    private void uploadVideoToUrl(String uploadUrl, byte[] videoData) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Content-Type", "video/mp4")
                .header("Content-Range", "bytes 0-" + (videoData.length - 1) + "/" + videoData.length)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(videoData))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            logger.error("Failed to upload video to URL. Status: {}, Body: {}", response.statusCode(), response.body());
            throw new RuntimeException("Failed to upload video. Status: " + response.statusCode());
        }
    }


    }



