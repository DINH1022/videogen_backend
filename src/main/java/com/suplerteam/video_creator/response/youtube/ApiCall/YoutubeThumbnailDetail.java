package com.suplerteam.video_creator.response.youtube.ApiCall;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeThumbnailDetail {
    @JsonProperty("url")
    private String url;
    @JsonProperty("width")
    private Long width;
    @JsonProperty("height")
    private Long height;
}
