package com.suplerteam.video_creator.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TiktokStatsDTO {
    private String title;
    private String url;
    private String description;
    private String thumbnail;
    private LocalDateTime publishedAt;
    private Long numOfViews;
    private Integer numOfLikes;
    private Integer numOfComments;
    private Integer numOfShares;
    private String videoId;
}