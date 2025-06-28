package com.suplerteam.video_creator.response.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private String script;
    private String[] imagesSet;
    private String audioUrl;
    private String videoUrl;
    private LocalDateTime createdAt;
}
