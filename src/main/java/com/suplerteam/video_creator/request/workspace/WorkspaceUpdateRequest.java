package com.suplerteam.video_creator.request.workspace;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceUpdateRequest {
    private String script;
    @JsonProperty("imagesSet")
    private String[] imagesSet;
    private Long audioId;
    private String audioUrl;
    private String videoUrl;
    private String language;
    private String shortScript;
    private String writingStyle;
}