package com.suplerteam.video_creator.service.image;

import com.suplerteam.video_creator.request.image.TextToImageRequest;

import java.util.List;

public interface ImageService {
    String generateAnImage(TextToImageRequest req);
    List<String> generateImages(List<TextToImageRequest> requests) throws InterruptedException;
}
