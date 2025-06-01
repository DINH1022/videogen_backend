package com.suplerteam.video_creator.service.audio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.request.audio.ElevenLabsApiBody;
import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Qualifier("elevenLabs-AudioService")
public class ElevenLabsAudioServiceImpl implements AudioService{
    private final String URL="https://api.elevenlabs.io/v1/text-to-speech/JBFqnCBsd6RMkjVDRZzb?output_format=mp3_44100_128";
    private final String DEFAULT_MODEL="eleven_multilingual_v2";

    @Value("${myapp.parameters.eleven-labs-key}")
    private String API_KEY;


    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public InputStreamResource textToSpeech(
            TextToSpeechRequest req) throws JsonProcessingException {
        HttpHeaders headers=new HttpHeaders();
        headers.set("xi-api-key", API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        ElevenLabsApiBody elevenLabsApiBody=ElevenLabsApiBody.builder()
                .text(req.getText())
                .modelId(DEFAULT_MODEL)
                .build();

        String jsonBody=objectMapper.writeValueAsString(elevenLabsApiBody);

        HttpEntity<String> entity=new HttpEntity<>(jsonBody,headers);
        RestTemplate restTemplate=new RestTemplate();
        ResponseEntity<byte[]> res=restTemplate.exchange(
                URL, HttpMethod.POST,entity,byte[].class
        );
        if(res.getStatusCode()!=HttpStatus.OK || res.getBody()==null){
            throw new RuntimeException("");
        }
        byte[] audioBytes=res.getBody();
        return new InputStreamResource(new ByteArrayResource(audioBytes));
    }
}
