package com.suplerteam.video_creator.response.wikipedia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class WikiSection {
    @JsonProperty("index")
    private int index;
    @JsonProperty("line")
    private String title;
    @JsonProperty("level")
    private int level;
}
