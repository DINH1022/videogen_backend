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
public class CambAITaskStatusApiResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("run_id")
    private String runId;
    @JsonProperty("exception_reason")
    private String exceptionReason;
    @JsonProperty("message")
    private String message;
}
