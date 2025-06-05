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
public class YoutubeStatistics {
    @JsonProperty("viewCount")
    private Long viewCount;
    @JsonProperty("likeCount")
    private Long likeCount;
    @JsonProperty("commentCount")
    private Long commentCount;
}
