package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.request.social_video_stats.UserVideosStatsRequest;
import com.suplerteam.video_creator.service.social_video_insights.SocialVideoInsightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/video-stats")
public class SocialVideoStatsController {

    @Autowired
    private SocialVideoInsightsService socialVideoInsightsService;
    @GetMapping("/youtube")
    public ResponseEntity<List<YoutubeStatsDTO>> getYoutubeVideosStats(
            @RequestParam(name = "page",defaultValue = "0")
            Integer page,
            @RequestParam(name = "size",defaultValue = "10")
            Integer size){
        //tech-debt: get username from context
        String username="vinh";
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
        //tech-debt: get username from context
        String username="vinh";
        return ResponseEntity.ok(socialVideoInsightsService
                .getTotalViewOfUploadedVideosOnYoutube(username));
    }
}
