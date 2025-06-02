package com.suplerteam.video_creator.request.video.shot_stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.lang.model.element.NestingKind;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShotStackOutput {
    public static final String VIDEO_FORMAT="mp4";
    public static final String DEFAULT_RESOLUTION="sd";
    @JsonProperty("format")
    private String format;
    @JsonProperty("resolution")
    private String resolution;

    public static ShotStackOutput buildFromCreateVideoRequest(CreateVideoRequest req){
        return ShotStackOutput.builder()
                .format(VIDEO_FORMAT)
                .resolution(DEFAULT_RESOLUTION)
                .build();
    }
}
