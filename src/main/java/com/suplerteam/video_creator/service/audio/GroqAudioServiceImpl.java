package com.suplerteam.video_creator.service.audio;

import com.suplerteam.video_creator.request.audio.GroqApiBody;
import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;

@Service
@Qualifier("groq-AudioService")
public class GroqAudioServiceImpl implements AudioService{

    @Autowired
    @Qualifier("groq-webClient")
    private WebClient.Builder webClientBuilder;

    @Override
    public InputStreamResource textToSpeech(TextToSpeechRequest req){
        GroqApiBody playAIApiBody=new GroqApiBody(req.getText(),req.getVoice());
        return webClientBuilder.build()
                .post()
                .bodyValue(playAIApiBody)
                .retrieve()
                .bodyToMono(InputStreamResource.class)
                .block();
    }

}
