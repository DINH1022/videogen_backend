package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import com.suplerteam.video_creator.service.audio.AudioService;
import com.suplerteam.video_creator.service.audio.AudioStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.suplerteam.video_creator.service.cloudinary.CloudinaryService;

import java.io.IOException;

@RestController
@RequestMapping("/audio")
public class AudioController {

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    @Qualifier("cambAI-AudioService")
    private AudioService audioService;

    @Autowired
    private AudioStorageService audioStorageService;

    @GetMapping("/generate")
    public ResponseEntity<String> textToSpeech (@RequestBody TextToSpeechRequest request)
        throws InterruptedException, IOException {
            InputStreamResource audioResource = audioService.textToSpeech(request);
            String uniqueStr = "gnkjewhiuhfuhahcj";
            String audioUrl = cloudinaryService.uploadAudio(audioResource,uniqueStr);
            audioStorageService.saveAudio(audioUrl, request);

            return ResponseEntity.ok(audioUrl);
    }
}
