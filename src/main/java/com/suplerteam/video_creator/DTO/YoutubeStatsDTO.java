package com.suplerteam.video_creator.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeStatsDTO {
    @JsonProperty("title")
    private String title;
    @JsonProperty("url")
    private String url;
    @JsonProperty("thumb_nail")
    private String thumbnail;
    @JsonProperty("number_of_views")
    private Long numOfViews;
    @JsonProperty("number_of_likes")
    private Long numOfLikes;
    @JsonProperty("number_of_comments")
    private Long numOfComments;
    @JsonProperty("published_at")
    private LocalDateTime publishedAt;

}

