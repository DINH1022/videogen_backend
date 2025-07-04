package com.suplerteam.video_creator.service.social_connection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Qualifier("YoutubeConnection-Service")
public class YoutubeConnection implements SocialAccountLinkingService {

    @Value("${myapp.parameters.google-client-id}")
    private String  GOOGLE_CLIENT_ID;
    @Value("${myapp.parameters.google-client-secret}")
    private String  GOOGLE_CLIENT_SECRET;
    @Value("${myapp.parameters.google-token-issue}")
    private String GOOGLE_TOKEN_SERVER_URL;
    @Value("${myapp.parameters.google-auth-url}")
    private String GOOGLE_USER_AUTH_URL;
    @Value("${myapp.parameters.google-callback-url}")
    private String GOOGLE_OAUTH_CALLBACK_URL;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    private WebClient.Builder getGoogleTokenIssueWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(GOOGLE_TOKEN_SERVER_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public String getAuthURL(Long userId){
        List<String> scopes= List.of("email","profile","https://www.googleapis.com/auth/youtube");
        String scopeParams=String.join(" ",scopes);
        return UriComponentsBuilder
                .fromPath(GOOGLE_USER_AUTH_URL)
                .queryParam("client_id",GOOGLE_CLIENT_ID)
                .queryParam("scope",scopeParams)
                .queryParam("redirect_uri",GOOGLE_OAUTH_CALLBACK_URL)
                .queryParam("response_type","code")
                .queryParam("prompt","consent")
                .queryParam("access_type","offline")
                .queryParam("state",userId)
                .toUriString();
    }

    @Override
    public String getRefreshTokenByAuthCode(String code){
        try{
            String response = getGoogleTokenIssueWebClientBuilder().build()
                    .post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue("client_id=" + GOOGLE_CLIENT_ID +
                            "&client_secret=" + GOOGLE_CLIENT_SECRET +
                            "&code=" + code +
                            "&grant_type=authorization_code" +
                            "&redirect_uri=" + GOOGLE_OAUTH_CALLBACK_URL)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode jsonNode=objectMapper.readTree(response);
            return jsonNode.path("refresh_token").asText();
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public Boolean connectToSocialAccount(Long userId,String authorizationCode) {
        String refreshToken=getRefreshTokenByAuthCode(authorizationCode);
        if(refreshToken==null || refreshToken.isEmpty()){
            return false;
        }
        User user=userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Not found user"));
        user.getSocialConnection().setYoutubeToken(refreshToken);
        userRepository.save(user);
        return true;
    }
}
