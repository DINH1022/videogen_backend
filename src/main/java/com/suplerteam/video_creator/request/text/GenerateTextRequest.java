package com.suplerteam.video_creator.request.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.enums.TextGenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTextRequest {
    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("type")
    private TextGenerationType type;

    @JsonProperty("writingStyle")
    private String writingStyle;

    @JsonProperty("language")
    private String language;

    @JsonProperty("shortScript")
    private String shortScript;
}
