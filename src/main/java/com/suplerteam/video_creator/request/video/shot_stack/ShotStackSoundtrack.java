package com.suplerteam.video_creator.request.video.shot_stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShotStackSoundtrack {
    public static final String DEFAULT_EFFECT="fadeInFadeOut";
    @JsonProperty("src")
    private String src;
    @JsonProperty("effect")
    private String effect;

    public static ShotStackSoundtrack buildFromCreateVideoRequest(
            CreateVideoRequest req){
        return ShotStackSoundtrack.builder()
                .src(req.getAudioUrl())
                .effect(ShotStackSoundtrack.DEFAULT_EFFECT)
                .build();
    }
}
