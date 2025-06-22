package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;
import com.suplerteam.video_creator.service.upload_social.VideoUploadService;
import com.suplerteam.video_creator.service.upload_to_social_client.YoutubeUploaderClientImpl;
import com.suplerteam.video_creator.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/social-upload-video")
public class UploadVideoToSocialController {

//    @Autowired
//    private VideoUploadService videoUploadService;

    @Autowired
    @Qualifier("YoutubeUploadServiceImpl")
    private VideoUploadService youtubeUploadService;

    @Autowired
    @Qualifier("TiktokUploadServiceImpl")
    private VideoUploadService tiktokUploadService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @PostMapping("/youtube-upload")
    public ResponseEntity<Boolean> uploadVideoToYoutube(
            @RequestBody SocialVideoUploadRequest req){
        //tech-debt: get from jwtdoes
        String username="vinh";
        req.setUsername(username);
        //var res=videoUploadService.upload(req);
        var res=youtubeUploadService.upload(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/tiktok-upload")
    public ResponseEntity<Boolean> uploadVideoToTiktok(
            @RequestBody SocialVideoUploadRequest req){
        User currentUser = authenticationUtil.getCurrentUser();
        req.setUsername(currentUser.getUsername());
        return ResponseEntity.ok(tiktokUploadService.upload(req));
    }

}
