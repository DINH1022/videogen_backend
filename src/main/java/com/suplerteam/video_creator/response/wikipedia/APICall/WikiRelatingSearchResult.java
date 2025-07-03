package com.suplerteam.video_creator.response.wikipedia.APICall;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WikiRelatingSearchResult {
    @JsonProperty("title")
    private String title;
    @JsonProperty("pageid")
    private Integer pageId;
    @JsonProperty("size")
    private Long size;
    @JsonProperty("wordcount")
    private Long wordCount;
    @JsonProperty("snippet")
    private String snippet;
}
