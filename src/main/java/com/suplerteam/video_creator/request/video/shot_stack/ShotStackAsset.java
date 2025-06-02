package com.suplerteam.video_creator.request.video.shot_stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.request.video.VideoImageSegment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShotStackAsset {
    public static final String IMAGE_TYPE="image";
    @JsonProperty("type")
    private String type;
    @JsonProperty("src")
    private String src;

    public static ShotStackAsset buildFromImageSegment(
            VideoImageSegment segment){
        return ShotStackAsset.builder()
                .type(IMAGE_TYPE)
                .src(segment.getUrl())
                .build();
    }
}
