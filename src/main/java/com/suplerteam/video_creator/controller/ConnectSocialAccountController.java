package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.service.social_connection.SocialConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("YoutubeConnection-Service")
    private SocialConnectionService youtubeConnectionService;

    @Autowired
    @Qualifier("TiktokConnection-Service")
    private SocialConnectionService tiktokConnectionService;

    private static final Logger log = LoggerFactory.getLogger(ConnectSocialAccountController.class);

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
        //tech-debt: replace by real front-end url
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:7171"))
                .build();
    }

    @GetMapping("/tiktok")
    public ResponseEntity<Void> redirectToTiktokOauth(
            @RequestParam(name = "user-id")Long userId){
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(tiktokConnectionService.getAuthURL(userId)))
                .build();
    }

//    @GetMapping("/tiktok-callback")
//    public ResponseEntity<Void> tiktokCallback(
//            @RequestParam("code")String code,
//            @RequestParam("state")Long userId) {
//        tiktokConnectionService.connectToSocialAccount(userId,code);
//        //tech-debt: replace by real front-end url
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .location(URI.create("https://ee9e-14-186-85-147.ngrok-free.app"))
//                .build();
//    }

    @GetMapping("/tiktok-callback")
    public ResponseEntity<Void> tiktokCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) Long userId,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String errorDescription) {

        if (error != null) {
            // Log the error from TikTok
            log.error("TikTok OAuth error: {} - {}", error, errorDescription);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("https://ee9e-14-186-85-147.ngrok-free.app/error?message=" + errorDescription))
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
                    .location(URI.create("https://ee9e-14-186-85-147.ngrok-free.app/error?message=Connection+failed"))
                    .build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("https://ee9e-14-186-85-147.ngrok-free.app/success"))
                .build();
    }

}
