package com.suplerteam.video_creator.service.image;

import com.suplerteam.video_creator.exception.RateLimitApiCallException;
import com.suplerteam.video_creator.request.image.TextToImageRequest;
import com.suplerteam.video_creator.request.image.TogetherApiBody;
import com.suplerteam.video_creator.response.togetherAI.TogetherAIApiResponse;
import com.suplerteam.video_creator.response.togetherAI.TogetherAIErrorApiResponse;
import com.suplerteam.video_creator.response.togetherAI.TogetherAIImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Qualifier("togetherAI-ImageService")
public class TogetherAIImageServiceImpl implements ImageService {
    @Autowired
    @Qualifier("togetherAI-webClient")
    private WebClient.Builder webClientBuilder;

    @Override
    public String generateAnImage(TextToImageRequest req) {
        TogetherApiBody body= TogetherApiBody.builder()
                .model(TogetherApiBody.DEFAULT_MODEL)
                .prompt(req.getText())
                .build();
        TogetherAIApiResponse emptyRes = TogetherAIApiResponse.builder()
                .imageResponses(List.of(
                        TogetherAIImageResponse.builder()
                                .url("")
                                .build()
                ))
                .build();

        TogetherAIApiResponse res=webClientBuilder.build()
                .post()
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(TogetherAIErrorApiResponse.class)
                                .flatMap(errRes -> {
                                    if (errRes.getError().getMessage().toLowerCase()
                                            .contains("rate limit specific")) {
                                        return Mono.error(new RateLimitApiCallException());
                                    }
                                    return Mono.error(new RuntimeException(errRes
                                            .getError().getMessage()));
                                })
                )
                .bodyToMono(TogetherAIApiResponse.class)
                .onErrorResume(RateLimitApiCallException.class, ex -> Mono.just(emptyRes))
                .block();
        List<TogetherAIImageResponse> imageResponses= res.getImageResponses();
        return imageResponses.getFirst().getUrl();
    }

    @Override
    public List<String> generateImages(List<TextToImageRequest> requests) throws InterruptedException {
        Long TIME_TO_SLEEP=1500L;
        List<String> urls = new ArrayList<>();
        for (TextToImageRequest request : requests) {
            String newUrl = "";
            while (newUrl.isEmpty()){
                newUrl=generateAnImage(request);
                Thread.sleep(TIME_TO_SLEEP);
            }
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
