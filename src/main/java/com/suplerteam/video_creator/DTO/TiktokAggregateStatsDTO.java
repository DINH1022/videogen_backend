package com.suplerteam.video_creator.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TiktokAggregateStatsDTO {
    private Long totalViews;
    private Long totalLikes;
    private Long totalComments;
    private Long totalShares;

    private Integer totalVideos;

    private Double averageViews;
    private Double averageLikes;
    private Double averageComments;
    private Double averageShares;

}
