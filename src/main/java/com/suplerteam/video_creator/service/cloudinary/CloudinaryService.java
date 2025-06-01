package com.suplerteam.video_creator.service.cloudinary;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;

public interface CloudinaryService {
    String uploadAudio(InputStreamResource resource,String fileName) throws IOException;
}
