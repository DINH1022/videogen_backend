package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import com.suplerteam.video_creator.request.image.TextToImageRequest;
import com.suplerteam.video_creator.service.audio.AudioService;
import com.suplerteam.video_creator.service.cloudinary.CloudinaryService;
import com.suplerteam.video_creator.service.image.ImageService;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class home {
    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    @Qualifier("clipdrop-ImageService")
    private ImageService imageService;


    @GetMapping
    ResponseEntity<List<String>> home(
            @RequestParam(name = "text")String text
    ) throws IOException, InterruptedException {
        List<TextToImageRequest> requests=new ArrayList<>();
        TextToImageRequest req1=TextToImageRequest
                .builder()
                .text("A sprawling futuristic city with neon lights, flying cars, " +
                        "and towering skyscrapers, bathed in warm hues of orange and " +
                        "purple from a setting sun. " +
                        "The sky is partly cloudy with reflections on glass " +
                        "buildings and bustling streets below")
                .build();
        TextToImageRequest req3=TextToImageRequest
                .builder()
                .text("A cyberpunk female warrior in a glowing suit of armor, standing in a dimly lit, rainy alley filled with holographic signs, puddles reflecting colorful lights, and steam rising from sewer grates. Her expression is calm but alert")
                .build();
        TextToImageRequest req2=TextToImageRequest
                .builder()
                .text("A tranquil Japanese garden with a small koi pond, a red wooden bridge, cherry blossom trees in full bloom, and soft sunlight filtering through the petals. There are a few stepping stones and a traditional tea house in the background")
                .build();
        requests.add(req1);
        requests.add(req2);
        requests.add(req3);
        List<String> urls=imageService.generateImages(requests);
        return ResponseEntity.ok(urls);
    }
}
