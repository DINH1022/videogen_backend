package com.suplerteam.video_creator.service.social_connection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Qualifier("TiktokConnection-Service")
public class TiktokConnection implements SocialConnectionService {
    private static final Logger log = LoggerFactory.getLogger(TiktokConnection.class);

    @Value("${myapp.parameters.tiktok-client-key}")
    private String TIKTOK_CLIENT_KEY;

    @Value("${myapp.parameters.tiktok-client-secret}")
    private String TIKTOK_CLIENT_SECRET;

    private final String TIKTOK_AUTH_URL = "https://www.tiktok.com/v2/auth/authorize/";
    private final String TIKTOK_TOKEN_URL = "https://open.tiktokapis.com/v2/oauth/token/";
    private final String TIKTOK_REDIRECT_URI = "http://localhost:8080/connect/tiktok-callback";

    // Store PKCE code verifiers by user ID
    private final Map<Long, String> codeVerifiers = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("tiktok-webClient")
    private WebClient.Builder tiktokAuthWebClientBuilder;

    @Override
    public String getAuthURL(Long userId) {
        log.info("Generating TikTok auth URL for user ID: {}", userId);

        // Generate code verifier - direct random string using only allowed characters
        String codeVerifier = generateCodeVerifier();
        codeVerifiers.put(userId, codeVerifier);
        log.info("Generated code verifier: {}", codeVerifier);

        // Generate code challenge by SHA-256 hashing the verifier
        String codeChallenge = generateCodeChallenge(codeVerifier);
        log.info("Generated code challenge: {}", codeChallenge);

        // Build authorization URL
        String authUrl = UriComponentsBuilder.fromUriString(TIKTOK_AUTH_URL)
                .queryParam("client_key", TIKTOK_CLIENT_KEY)
                .queryParam("response_type", "code")
                .queryParam("scope", "user.info.basic,video.upload,video.publish,video.list")
                .queryParam("redirect_uri", TIKTOK_REDIRECT_URI)
                .queryParam("state", userId)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .build()
                .toUriString();

        log.info("Generated TikTok auth URL: {}", authUrl);
        return authUrl;
    }

    private String generateCodeVerifier() {
        // Generate a code verifier using only allowed characters: [A-Z] [a-z] [0-9] - . _ ~
        SecureRandom random = new SecureRandom();
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";

        // Length between 43-128 chars (using 64 as a good length)
        int length = 64;
        StringBuilder codeVerifier = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            codeVerifier.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }

        return codeVerifier.toString();
    }


    private String generateCodeChallenge(String codeVerifier) {
        try {
            // SHA-256 hash the code_verifier
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));

            // Convert to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error generating code challenge", e);
            throw new RuntimeException("Failed to generate code challenge", e);
        }
    }

    @Transactional
    @Override
    public Boolean connectToSocialAccount(Long userId, String authorizationCode) {
        log.info("Connecting TikTok account for user ID: {}", userId);

        // Get user's code verifier - use remove to get and delete in one operation
        String codeVerifier = codeVerifiers.remove(userId);
        if (codeVerifier == null) {
            log.error("No code verifier found for user ID: {}", userId);
            return false;
        }
        log.info("Retrieved code verifier for token exchange: {}", codeVerifier);

        try {
            String accessToken = exchangeCodeForToken(authorizationCode, codeVerifier);
            if (accessToken == null || accessToken.isEmpty()) {
                log.error("Failed to obtain access token from TikTok");
                return false;
            }

            // Store the token with the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            user.getSocialConnection().setTiktokToken(accessToken);
            userRepository.save(user);

            log.info("Successfully saved TikTok token for user ID: {}", userId);
            return true;

        } catch (Exception e) {
            log.error("Error connecting TikTok account: {}", e.getMessage(), e);
            return false;
        }
    }

    private String exchangeCodeForToken(String authorizationCode, String codeVerifier) {
        log.info("Exchanging authorization code for token");
        try {
            // Create form data for the token exchange
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("client_key", TIKTOK_CLIENT_KEY);
            formData.add("client_secret", TIKTOK_CLIENT_SECRET);
            formData.add("code", authorizationCode);
            formData.add("grant_type", "authorization_code");
            formData.add("code_verifier", codeVerifier);
            formData.add("redirect_uri", TIKTOK_REDIRECT_URI);

            log.info("Sending token request to TikTok with payload: {}", formData);

            // Send request to TikTok token endpoint
            String response = tiktokAuthWebClientBuilder.build()
                    .post()
                    .uri(TIKTOK_TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("TikTok token response: {}", response);
            JsonNode jsonResponse = objectMapper.readTree(response);

            // Check for errors
            if (jsonResponse.has("error") && !jsonResponse.path("error").isNull()) {
                String errorCode = jsonResponse.path("error").path("code").asText();
                String errorMsg = jsonResponse.path("error").path("message").asText();
                log.error("TikTok API error: {} - {}", errorCode, errorMsg);
                return null;
            }

            // Extract and return the access token
            if (jsonResponse.has("access_token")) {
                return jsonResponse.path("access_token").asText();
            } else if (jsonResponse.has("data") && jsonResponse.path("data").has("access_token")) {
                return jsonResponse.path("data").path("access_token").asText();
            } else {
                log.error("No access token found in response: {}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("Error exchanging code for token", e);
            throw new RuntimeException("Failed to exchange code for token", e);
        }
    }

    @Override
    public String getRefreshTokenByAuthCode(String authorizationCode) {
        throw new UnsupportedOperationException("Not implemented for TikTok");
    }
}