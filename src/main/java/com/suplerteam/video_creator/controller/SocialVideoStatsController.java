package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.DTO.TiktokStatsDTO;
import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.request.social_video_stats.UserVideosStatsRequest;
import com.suplerteam.video_creator.service.social_video_insights.SocialVideoInsightsService;
import com.suplerteam.video_creator.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/video-stats")
public class SocialVideoStatsController {

    @Autowired
    private SocialVideoInsightsService socialVideoInsightsService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @GetMapping("/youtube")
    public ResponseEntity<List<YoutubeStatsDTO>> getYoutubeVideosStats(
            @RequestParam(name = "page",defaultValue = "0")
            Integer page,
            @RequestParam(name = "size",defaultValue = "10")
            Integer size){
        User currentUser = authenticationUtil.getCurrentUser();
        String username=currentUser.getUsername();
        UserVideosStatsRequest req=UserVideosStatsRequest.builder()
                .username(username)
                .page(page)
                .size(size)
                .build();
        return ResponseEntity.ok(socialVideoInsightsService
                .getStatsOfYoutubeVideosOfUser(req));
    }


    @GetMapping("/youtube-total-views")
    public ResponseEntity<Long> getYoutubeVideosTotalViews(){
        User currentUser = authenticationUtil.getCurrentUser();
        String username=currentUser.getUsername();
        return ResponseEntity.ok(socialVideoInsightsService
                .getTotalViewOfUploadedVideosOnYoutube(username));
    }

    @GetMapping("/tiktok")
    public ResponseEntity<List<TiktokStatsDTO>> getTiktokVideosStats(
            @RequestParam(name = "page", defaultValue = "0")
            Integer page,
            @RequestParam(name = "size", defaultValue = "10")
            Integer size) {
        User currentUser = authenticationUtil.getCurrentUser();
        String username = currentUser.getUsername();
        UserVideosStatsRequest req = UserVideosStatsRequest.builder()
                .username(username)
                .page(page)
                .size(size)
                .build();
        return ResponseEntity.ok(socialVideoInsightsService.getStatsOfTiktokVideosOfUser(req));
    }

    @GetMapping("/tiktok-total-views")
    public ResponseEntity<Long> getTiktokVideosTotalViews() {
        User currentUser = authenticationUtil.getCurrentUser();
        String username = currentUser.getUsername();
        return ResponseEntity.ok(socialVideoInsightsService.getTotalViewOfUploadedVideosOnTiktok(username));
    }


}
