package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import com.suplerteam.video_creator.request.image.TextToImageRequest;
import com.suplerteam.video_creator.request.text.GenerateTextRequest;
import com.suplerteam.video_creator.service.audio.AudioService;
import com.suplerteam.video_creator.service.cloudinary.CloudinaryService;
import com.suplerteam.video_creator.service.image.ImageService;
import com.suplerteam.video_creator.service.text.TextAIService;
import com.suplerteam.video_creator.service.user.UserService;
import com.suplerteam.video_creator.service.wikipedia.WikipediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class home {
    @Autowired
    @Qualifier("Gemini-TextService")
    private TextAIService textAIService;


    @GetMapping
    ResponseEntity<String> home(
            @RequestParam(name = "text")String text
    ) throws IOException, InterruptedException {
        return ResponseEntity.ok("Good day");
    }
}
