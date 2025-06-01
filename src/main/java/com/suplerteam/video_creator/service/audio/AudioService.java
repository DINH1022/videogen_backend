package com.suplerteam.video_creator.service.audio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import org.springframework.core.io.InputStreamResource;

public interface AudioService {
    InputStreamResource textToSpeech(TextToSpeechRequest req) throws JsonProcessingException;
}
