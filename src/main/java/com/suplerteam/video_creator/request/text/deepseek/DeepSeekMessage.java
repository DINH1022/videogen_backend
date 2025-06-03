package com.suplerteam.video_creator.request.text.deepseek;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.request.text.GenerateTextRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekMessage {
    @JsonProperty("role")
    private String role;
    @JsonProperty("content")
    private String content;

    public static List<DeepSeekMessage> buildFromGenerateTextRequest(GenerateTextRequest req){
        DeepSeekMessage systemMessage=DeepSeekMessage.builder()
                .role("system")
                .content("You are a wonderful assistant")
                .build();
        DeepSeekMessage userMessage=DeepSeekMessage.builder()
                .role("user")
                .content(req.getPrompt())
                .build();
        return List.of(systemMessage,userMessage);
    }
}
