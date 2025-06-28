package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import com.suplerteam.video_creator.service.video.VideoService;
import com.suplerteam.video_creator.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;
    
    @Autowired
    private AuthenticationUtil authenticationUtil;

    @PostMapping("/create/{workspaceId}")
    public ResponseEntity<String> createVideo(
            @PathVariable String workspaceId,
            @RequestBody CreateVideoRequest req) throws InterruptedException, IOException {
        User currentUser = authenticationUtil.getCurrentUser();
        return ResponseEntity.ok(videoService.createVideo(req, workspaceId, currentUser.getUsername()));
    }
}
