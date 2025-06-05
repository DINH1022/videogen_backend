package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.service.social_connection.SocialConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/connect")
public class ConnectSocialAccountController {

    @Autowired
    @Qualifier("YoutubeConnection-Service")
    private SocialConnectionService youtubeConnectionService;



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
}
