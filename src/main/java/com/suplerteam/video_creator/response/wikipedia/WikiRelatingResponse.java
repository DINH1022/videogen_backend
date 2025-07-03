package com.suplerteam.video_creator.response.wikipedia;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.response.wikipedia.APICall.WikiRelatingSearchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WikiRelatingResponse {
    @JsonProperty("url")
    private String url;
    @JsonProperty("snippet")
    private String snippet;

    public static WikiRelatingResponse createFromAPICallSearch(WikiRelatingSearchResult res){
        return WikiRelatingResponse.builder()
                .url("https://en.wikipedia.org/wiki/"+
                        res.getTitle().replace(" ","_"))
                .snippet(res.getSnippet())
                .build();
    }
}
