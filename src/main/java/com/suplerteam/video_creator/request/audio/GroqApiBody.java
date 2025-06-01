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
public class GroqApiBody {


    @JsonProperty("model")
    private String model;
    @JsonProperty("input")
    private String text;
    @JsonProperty("voice")
    private String voice;
    @JsonProperty("response_format")
    private String outputFormat;

    public GroqApiBody(String text, String voice){
        this.text = text;
        this.voice = (voice != null && !voice.isEmpty()) ? voice : "Fritz-PlayAI";
        this.model = "playai-tts";
        this.outputFormat = "mp3";
    }
}
