package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.request.video.CreateVideoRequest;

import com.suplerteam.video_creator.service.video.VideoService;
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

    @PostMapping("/create")
    public ResponseEntity<String> createVideo(
            @RequestBody CreateVideoRequest req) throws InterruptedException, IOException {
        return ResponseEntity.ok(videoService.createVideo(req));
    }
}
