package com.suplerteam.video_creator.request.audio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElevenLabsApiBody {
    @JsonProperty("text")
    private String text;
    @JsonProperty("model_id")
    private String modelId;
}
