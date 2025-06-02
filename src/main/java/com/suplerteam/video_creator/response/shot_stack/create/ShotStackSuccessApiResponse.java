package com.suplerteam.video_creator.response.shot_stack.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShotStackSuccessApiResponse {
    @JsonProperty("success")
    private String success;
    @JsonProperty("message")
    private String message;
    @JsonProperty("response")
    private ShotStackResponseApiRes response;
}
