package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import com.suplerteam.video_creator.service.audio.AudioService;
import com.suplerteam.video_creator.service.cloudinary.CloudinaryService;
import com.suplerteam.video_creator.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class home {
    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    @Qualifier("groq-AudioService")
    private AudioService audioService;


    @GetMapping
    ResponseEntity<String> home(
            @RequestParam(name = "text")String text
    ) throws IOException {
        TextToSpeechRequest req= TextToSpeechRequest
                .builder()
                .text(text)
                .build();
        InputStreamResource res=audioService.textToSpeech(req);
        String url=cloudinaryService.uploadAudio(res,"sdasda12dlajsdlkasjldasldajsldas2131231");
        return ResponseEntity.ok(url);
    }
}
