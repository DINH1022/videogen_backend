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
public class DeepSeekApiBody {
    @JsonProperty("model")
    private String model;
    @JsonProperty("messages")
    private List<DeepSeekMessage> messages;

    public static DeepSeekApiBody buildFromGenerateTextRequest(GenerateTextRequest req){
        return DeepSeekApiBody.builder()
                .model("deepseek/deepseek-r1-0528-qwen3-8b:free")
                .messages(DeepSeekMessage.buildFromGenerateTextRequest(req))
                .build();
    }

}
