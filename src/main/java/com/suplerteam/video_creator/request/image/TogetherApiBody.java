package com.suplerteam.video_creator.request.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TogetherApiBody {
    public static final String DEFAULT_MODEL="black-forest-labs/FLUX.1-schnell-Free";
    @JsonProperty("model")
    private String model;
    @JsonProperty("prompt")
    private String prompt;
}
