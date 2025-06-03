package com.suplerteam.video_creator.request.text.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.request.text.GenerateTextRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiContent {
    @JsonProperty("parts")
    private List<GeminiPart> parts;

    public static List<GeminiContent> buildFromGenerateTextRequest(GenerateTextRequest req){
        GeminiContent geminiContent=GeminiContent
                .builder()
                .parts(GeminiPart.buildFromGenerateTextRequest(req))
                .build();
        return List.of(geminiContent);
    }
}
