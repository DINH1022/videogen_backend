package com.suplerteam.video_creator.request.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVideoRequest {
    @JsonProperty("image_segments")
    private List<VideoImageSegment> imageSegments;
    @JsonProperty("audio")
    private String audioUrl;
    @JsonProperty("title")
    private String title;
}
