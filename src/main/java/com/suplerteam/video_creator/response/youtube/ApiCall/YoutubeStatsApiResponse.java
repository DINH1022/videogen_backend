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
public class YoutubeStatsApiResponse {
    @JsonProperty("snippet")
    private YoutubeSnippet snippet=new YoutubeSnippet();
    @JsonProperty("statistics")
    private YoutubeStatistics statistics;

    public String getThumbnail(){
        return snippet.getThumbnails().getThumbnail();
    }
}
