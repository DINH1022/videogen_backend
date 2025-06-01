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
public class TextToSpeechRequest {
    @JsonProperty("text")
    private String text;
    @JsonProperty("voice")
    private String voice;
    @JsonProperty("language")
    private String language;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("age")
    private String age;
}
