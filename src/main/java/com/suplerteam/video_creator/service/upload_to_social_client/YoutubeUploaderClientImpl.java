package com.suplerteam.video_creator.service.upload_to_social_client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.client.auth.oauth2.Credential;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.net.URI;

@Service
@Qualifier("youtube-uploader-Service")
public class YoutubeUploaderClientImpl implements VideoUploaderClient {
    @Value("${spring.application.name}")
    private String  APPLICATION_NAME;
    @Value("${myapp.parameters.google-client-id}")
    private String  GOOGLE_CLIENT_ID;
    @Value("${myapp.parameters.google-client-secret}")
    private String  GOOGLE_CLIENT_SECRET;
    @Value("${myapp.parameters.google-token-issue}")
    private String GOOGLE_TOKEN_SERVER_URL;
    @Value("${myapp.parameters.youtube-stats-url}")
    private String YOUTUBE_STATS_URL;
    @Value("${myapp.parameters.google-api-key}")
    private String GOOGLE_API_KEY;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private String getRefreshTokenByUsername(String username){
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundException("Not found user"));
        return user.getSocialConnection().getYoutubeToken();
    }
    private Credential authorize(String username) throws Exception {
        String refreshToken = getRefreshTokenByUsername(username);
        GoogleTokenResponse tokenResponse = new GoogleRefreshTokenRequest(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                refreshToken,
                GOOGLE_CLIENT_ID,
                GOOGLE_CLIENT_SECRET
        ).execute();

        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new NetHttpTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setClientAuthentication(
                        new ClientParametersAuthentication(GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET)
                )
                .setTokenServerUrl(new GenericUrl(GOOGLE_TOKEN_SERVER_URL))
                .build()
                .setAccessToken(tokenResponse.getAccessToken())
                .setRefreshToken(refreshToken);
    }
    @Override
    public String uploadVideo(SocialVideoUploadRequest req) {
        try{
            Credential credential = authorize(req.getUsername());

            YouTube youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
            ).setApplicationName(APPLICATION_NAME).build();

            String privacyLevel=req.getPrivacyLevel()==null||req.getPrivacyLevel().isEmpty()?
                    "public":req.getPrivacyLevel();
            String title=req.getTitle();
            String description=req.getDescription();
            Video videoMetadata = new Video();

            VideoStatus status = new VideoStatus();
            status.setPrivacyStatus(privacyLevel);
            status.setSelfDeclaredMadeForKids(false);
            videoMetadata.setStatus(status);
            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(title);
            snippet.setDescription(description);
            snippet.setTags(Arrays.asList("spring", "boot", "upload", "youtube"));
            snippet.setCategoryId("22");
            videoMetadata.setSnippet(snippet);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(req.getUrl()))
                    .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            InputStream inputStream = response.body();
            long contentLength = response.headers()
                    .firstValueAsLong("Content-Length")
                    .orElse(-1);
            InputStreamContent mediaContent = new InputStreamContent("video/mp4", inputStream);
            if (contentLength > 0) {
                mediaContent.setLength(contentLength);
            }
            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,statistics,status", videoMetadata, mediaContent);
            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();
            uploader.setDirectUploadEnabled(false);
            Video returnedVideo = videoInsert.execute();
            return returnedVideo.getId();
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


}
