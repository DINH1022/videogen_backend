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
public class GeminiPart {
    @JsonProperty("text")
    private String text;

    public static List<GeminiPart> buildFromGenerateTextRequest(GenerateTextRequest req){
        GeminiPart geminiPart=GeminiPart.builder()
                .text(req.getPrompt())
                .build();
        return List.of(geminiPart);
    }
}
