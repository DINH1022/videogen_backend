package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.DTO.TiktokStatsDTO;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import com.suplerteam.video_creator.service.social_video_insights.TiktokVideoService;
import com.suplerteam.video_creator.service.video.VideoService;
import com.suplerteam.video_creator.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;
    
    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private TiktokVideoService tiktokVideoService;

    @PostMapping("/create/{workspaceId}")
    public ResponseEntity<String> createVideo(
            @PathVariable String workspaceId,
            @RequestBody CreateVideoRequest req) throws InterruptedException, IOException {
        User currentUser = authenticationUtil.getCurrentUser();
        return ResponseEntity.ok(videoService.createVideo(req, workspaceId, currentUser.getUsername()));
    }

    @GetMapping("/tiktok-videos")
    public ResponseEntity<List<TiktokStatsDTO>> getUserTiktokVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = authenticationUtil.getCurrentUser();
        return ResponseEntity.ok(tiktokVideoService.getUserTiktokVideos(currentUser));
    }

    @GetMapping("/tiktok-videos/{id}")
    public ResponseEntity<TiktokStatsDTO> getTiktokVideoDetail(@PathVariable Long id) {
        User currentUser = authenticationUtil.getCurrentUser();
        return ResponseEntity.ok(tiktokVideoService.getTiktokVideoDetail(id, currentUser));
    }


}
