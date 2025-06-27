package com.suplerteam.video_creator.response.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceSummaryResponse {
    private Long id;
    private String title;
    private String description;
}
