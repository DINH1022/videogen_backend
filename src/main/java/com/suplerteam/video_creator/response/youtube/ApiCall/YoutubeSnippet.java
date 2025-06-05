package com.suplerteam.video_creator.response.youtube.ApiCall;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeSnippet {
    @JsonProperty("publishedAt")
    private OffsetDateTime publishedAt;
    @JsonProperty("title")
    private String title;
    @JsonProperty("thumbnails")
    private YoutubeThumbnail thumbnails=new YoutubeThumbnail();


}
