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
public class GeminiApiBody {
    @JsonProperty("contents")
    private List<GeminiContent> contents;

    public static GeminiApiBody buildFromGenerateTextRequest(GenerateTextRequest req){
        return  GeminiApiBody
                .builder()
                .contents(GeminiContent.buildFromGenerateTextRequest(req))
                .build();
    }
}
