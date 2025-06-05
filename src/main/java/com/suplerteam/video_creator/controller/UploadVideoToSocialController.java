package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;
import com.suplerteam.video_creator.service.upload_social.VideoUploadService;
import com.suplerteam.video_creator.service.upload_to_social_client.YoutubeUploaderClientImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/social-upload-video")
public class UploadVideoToSocialController {

    @Autowired
    private VideoUploadService videoUploadService;


    @PostMapping("/youtube-upload")
    public ResponseEntity<Boolean> uploadVideoToYoutube(
            @RequestBody SocialVideoUploadRequest req){
        //tech-debt: get from jwtdoes
        String username="vinh";
        req.setUsername(username);
        var res=videoUploadService.upload(req);
        return ResponseEntity.ok(res);
    }

}
