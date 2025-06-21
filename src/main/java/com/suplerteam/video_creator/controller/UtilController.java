package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.service.cloudinary.CloudinaryService;
import com.suplerteam.video_creator.util.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/utils")
@RestController
public class UtilController {

    @Autowired
    private UtilityService utilityService;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload-video-to-cloudinary")
    public ResponseEntity<String> uploadAVideoToCloudinary(
            @RequestParam("video")MultipartFile file
            ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        InputStreamResource resource=new InputStreamResource(file.getInputStream());
        String uuidName=utilityService.getUUID();
        String url=cloudinaryService.uploadVideo(resource,uuidName);
        return ResponseEntity.ok(url);
    }
}
