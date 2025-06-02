package com.suplerteam.video_creator.response.togetherAI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TogetherAIErrorApiResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("error")
    private TogetherAIErrorDetails error;
}
