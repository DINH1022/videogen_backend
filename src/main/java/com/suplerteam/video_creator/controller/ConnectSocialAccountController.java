package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.response.AccountSocialConnectionResponse;
import com.suplerteam.video_creator.service.social_connection.SocialAccountLinkingService;
import com.suplerteam.video_creator.service.social_connection.SocialConnectionStatusService;
import com.suplerteam.video_creator.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;

@RestController
@RequestMapping("/connect")
public class ConnectSocialAccountController {

    @Autowired
    private AuthenticationUtil authenticationUtil;
    @Autowired
    @Qualifier("YoutubeConnection-Service")
    private SocialAccountLinkingService youtubeConnectionService;

    @Autowired
    @Qualifier("TiktokConnection-Service")
    private SocialAccountLinkingService tiktokConnectionService;

    @Autowired
    private SocialConnectionStatusService socialConnectionStatusService;

    @Value("${myapp.parameters.front-end.base-url}")
    private String FRONT_END_BASE_URL;

    private static final Logger log = LoggerFactory.getLogger(ConnectSocialAccountController.class);

    @GetMapping("/status")
    public ResponseEntity<AccountSocialConnectionResponse> getAccountLinkingStatus(){
        String username=authenticationUtil.getCurrentUsername();
        return ResponseEntity.ok(socialConnectionStatusService
                .getAccountConnectionStatus(username));
    }

    @GetMapping("/youtube")
    public ResponseEntity<Void> redirectToYoutubeOauth(
            @RequestParam(name = "user-id")Long userId){
        System.out.println(youtubeConnectionService.getAuthURL(userId));
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(youtubeConnectionService.getAuthURL(userId)))
                .build();
    }

    @GetMapping("/youtube-callback")
    public ResponseEntity<Void> youtubeCallback(
            @RequestParam("code")String code,
            @RequestParam("state")Long userId) {
        youtubeConnectionService.connectToSocialAccount(userId,code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(FRONT_END_BASE_URL+"?status=success"))
                .build();
    }

    @GetMapping("/tiktok")
    public ResponseEntity<Void> redirectToTiktokOauth (){
        User currentUser = authenticationUtil.getCurrentUser();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(tiktokConnectionService.getAuthURL(currentUser.getId())))
                .build();
    }

    @GetMapping("/tiktok-callback")
    public ResponseEntity<Void> tiktokCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) Long userId,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String errorDescription) {

        if (error != null) {
            log.error("TikTok OAuth error: {} - {}", error, errorDescription);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:5173/error?message=" + errorDescription))
                    .build();
        }

        try {
            boolean success = tiktokConnectionService.connectToSocialAccount(userId, code);
            if (!success) {
                log.error("Failed to connect TikTok account for user ID: {}", userId);
            } else {
                log.info("Successfully connected TikTok account for user ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("Error processing TikTok callback", e);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:5173/error?message=Connection+failed"))
                    .build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:5173/success"))
                .build();
    }

}
