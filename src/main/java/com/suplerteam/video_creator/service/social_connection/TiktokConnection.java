package com.suplerteam.video_creator.service.social_connection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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
    private final String TIKTOK_OAUTH_CALLBACK_URL = "https://ee9e-14-186-85-147.ngrok-free.app/connect/tiktok-callback";

    // Store code verifiers for each user session
    private final Map<String, String> codeVerifiers = new HashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }


    private String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate code challenge", e);
        }
    }

    @Override
    public String getAuthURL(Long userId) {
        String csrfState = userId.toString();

        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        codeVerifiers.put(csrfState, codeVerifier);

        String tiktok_url = UriComponentsBuilder
                .fromHttpUrl(TIKTOK_AUTH_URL)
                .queryParam("client_key", TIKTOK_CLIENT_KEY)
                .queryParam("scope", "user.info.basic,video.upload,video.publish")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", TIKTOK_OAUTH_CALLBACK_URL)
                .queryParam("state", csrfState)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .build(true) // true enables encoding
                .toUriString();
        log.info("Generated TikTok auth URL: {}", tiktok_url);
        return tiktok_url;
    }

    @Override
    public String getRefreshTokenByAuthCode(String code) {
        try {
            // Extract state from the controller's request parameters to get code verifier
            String state = null;
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (request.getParameter("state") != null) {
                state = request.getParameter("state");
            }

            String codeVerifier = codeVerifiers.get(state);
            if (codeVerifier == null) {
                throw new RuntimeException("Code verifier not found for state: " + state);
            }

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("client_key", TIKTOK_CLIENT_KEY);
            formData.add("client_secret", TIKTOK_CLIENT_SECRET);
            formData.add("code", code);
            formData.add("grant_type", "authorization_code");
            formData.add("redirect_uri", TIKTOK_OAUTH_CALLBACK_URL);
            formData.add("code_verifier", codeVerifier);

            // Clean up after use
            codeVerifiers.remove(state);

            String response = WebClient.builder()
                    .baseUrl(TIKTOK_TOKEN_URL)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .build()
                    .post()
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("TikTok token response: {}", response);
            JsonNode jsonNode = objectMapper.readTree(response);

            // Check if response directly contains refresh_token (no data wrapper)
            if (jsonNode.has("refresh_token")) {
                return jsonNode.path("refresh_token").asText();
            } else if (jsonNode.has("data") && jsonNode.get("data").has("refresh_token")) {
                // Fallback to check if response has data wrapper
                return jsonNode.path("data").path("refresh_token").asText();
            } else {
                log.error("Unexpected TikTok response format: {}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to exchange TikTok authorization code", e);
            throw new RuntimeException("Failed to exchange TikTok authorization code: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public Boolean connectToSocialAccount(Long userId, String authorizationCode) {
        String refreshToken = getRefreshTokenByAuthCode(authorizationCode);
        if (refreshToken == null || refreshToken.isEmpty()) {
            return false;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found user"));
        user.getSocialConnection().setTiktokToken(refreshToken);
        userRepository.save(user);
        return true;
    }
}