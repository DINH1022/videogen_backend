package com.suplerteam.video_creator.request.social_video_stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserVideosStatsRequest {
    private String username;
    private Integer page;
    private Integer size;
}
