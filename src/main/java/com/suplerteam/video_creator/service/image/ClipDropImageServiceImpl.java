package com.suplerteam.video_creator.service.image;


import com.suplerteam.video_creator.request.image.TextToImageRequest;
import com.suplerteam.video_creator.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("clipdrop-ImageService")
public class ClipDropImageServiceImpl implements ImageService{
    @Autowired
    @Qualifier("clipdrop-webClient")
    private WebClient.Builder webClientBuilder;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public String generateAnImage(TextToImageRequest req) throws IOException {
        int MAX_BUFFER=10*1024*1024;
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("prompt",req.getText());
        byte[] imageBytes = webClientBuilder
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(MAX_BUFFER))
                        .build())
                .build()
                .post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.IMAGE_PNG)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(imageBytes));
        return cloudinaryService.uploadImage(resource,"231230123-12031-2312-");
    }

    @Override
    public List<String> generateImages(List<TextToImageRequest> requests) throws IOException {
        List<String> urls = new ArrayList<>();
        for (TextToImageRequest request : requests) {
            String newUrl =this.generateAnImage(request);
            urls.add(newUrl);
        }
        return urls;
    }

    @Override
    public List<String> generateImagesFromText(String text) throws InterruptedException, IOException {
        String[] sentences = text.split("[.!?]+");

        List<TextToImageRequest> requests = new ArrayList<>();
        for (String sentence : sentences) {
            String trimmedSentence = sentence.trim();
            if (!trimmedSentence.isEmpty()) {
                requests.add(new TextToImageRequest(trimmedSentence));
            }
        }
        return generateImages(requests);
    }
}
