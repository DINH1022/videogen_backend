package com.suplerteam.video_creator.response.CambAI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambAIErrorApiResponse {
    @JsonProperty("status_code")
    private String statusCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("payload")
    private String payload;
}
