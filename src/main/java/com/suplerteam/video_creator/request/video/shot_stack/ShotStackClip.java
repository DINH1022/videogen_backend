package com.suplerteam.video_creator.request.video.shot_stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.enums.ImageEffect;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import com.suplerteam.video_creator.request.video.VideoImageSegment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PublicKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShotStackClip {
    public static final Double DEFAULT_SCALE=1D;
    public static final String DEFAULT_POSITION="center";

    @JsonProperty("asset")
    private ShotStackAsset asset;
    @JsonProperty("start")
    private Integer start;
    @JsonProperty("length")
    private Integer length;
    @JsonProperty("scale")
    private Double scale;
    @JsonProperty("position")
    private String position;
    @JsonProperty("effect")
    private String effect;

    public static Integer calculateLength(Integer start,Integer end){
        return end-start;
    }
    public static ShotStackClip buildFromCreateVideoRequest(
            VideoImageSegment segment){
        ShotStackAsset newAsset=ShotStackAsset.buildFromImageSegment(segment);
        return ShotStackClip.builder()
                .asset(newAsset)
                .start(segment.getStart())
                .length(calculateLength(segment.getStart(),segment.getEnd()))
                .scale(DEFAULT_SCALE)
                .position(DEFAULT_POSITION)
                .effect(segment.getEffect()!=null
                        ?segment.getEffect(): ImageEffect.ZOOM_IN_SLOW)
                .build();
    }
}
