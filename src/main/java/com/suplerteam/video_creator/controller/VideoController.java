package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import com.suplerteam.video_creator.service.video.VideoService;
import com.suplerteam.video_creator.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;
    
    @Autowired
    private AuthenticationUtil authenticationUtil;

    @PostMapping("/create")
    public ResponseEntity<String> createVideo(
            @RequestBody CreateVideoRequest req) throws InterruptedException, IOException {
        User currentUser = authenticationUtil.getCurrentUser();
        return ResponseEntity.ok(videoService.createVideo(req, currentUser.getUsername()));
    }
}
