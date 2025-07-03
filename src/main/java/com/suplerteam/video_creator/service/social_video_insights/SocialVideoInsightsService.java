package com.suplerteam.video_creator.service.social_video_insights;

import com.suplerteam.video_creator.DTO.TiktokStatsDTO;
import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.request.social_video_stats.UserVideosStatsRequest;

import java.util.List;

public interface SocialVideoInsightsService {
    List<YoutubeStatsDTO> getStatsOfYoutubeVideosOfUser(UserVideosStatsRequest req);
    Long getTotalViewOfUploadedVideosOnYoutube(String username);
    List<TiktokStatsDTO> getStatsOfTiktokVideosOfUser(UserVideosStatsRequest req);
    Long getTotalViewOfUploadedVideosOnTiktok(String username);
}
