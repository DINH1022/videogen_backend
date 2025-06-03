package com.suplerteam.video_creator.service.text;

import com.suplerteam.video_creator.request.text.GenerateTextRequest;
import reactor.core.publisher.Mono;

public interface TextAIService {
    String generateContent(GenerateTextRequest req);
}
