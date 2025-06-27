package com.suplerteam.video_creator.service.image;

import com.suplerteam.video_creator.request.image.TextToImageRequest;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    String generateAnImage(TextToImageRequest req) throws IOException;
    List<String> generateImages(List<TextToImageRequest> requests) throws InterruptedException, IOException;
    List<String> generateImagesFromText(String text) throws InterruptedException, IOException;
}
