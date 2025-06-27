package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.request.image.TextToImageRequest;
import com.suplerteam.video_creator.response.CustomErrorResponse;
import com.suplerteam.video_creator.service.image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    //@Qualifier("clipdrop-ImageService")
    @Qualifier("gemini-ImageService")
    private ImageService imageService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateImage(@RequestBody TextToImageRequest request)
        throws InterruptedException, IOException {
            String imageUrl = imageService.generateAnImage(request);
            return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/generate-multiple")
    public ResponseEntity<?> generateMultipleImages(@RequestBody List<TextToImageRequest> requests)
        throws InterruptedException, IOException {
            List<String> imageUrls = imageService.generateImages(requests);
            return ResponseEntity.ok(imageUrls);
    }

    @PostMapping("/generate-from-story")
    public ResponseEntity<?> generateImagesFromText(@RequestBody TextToImageRequest request)
            throws InterruptedException, IOException {
        List<String> imageUrls = imageService.generateImagesFromText(request.getText());
        return ResponseEntity.ok(imageUrls);
    }
}