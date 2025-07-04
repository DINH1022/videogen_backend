package com.suplerteam.video_creator.service.social_video_insights;

import com.suplerteam.video_creator.DTO.TiktokStatsDTO;
import com.suplerteam.video_creator.entity.TiktokVideo;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.TiktokVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TiktokVideoService {

    @Autowired
    private TiktokVideoRepository tiktokVideoRepository;

    public List<TiktokStatsDTO> getUserTiktokVideos(User user) {
        List<TiktokVideo> videos = tiktokVideoRepository.findByTiktokUpload_User(user);

        return videos.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TiktokStatsDTO getTiktokVideoDetail(Long videoId, User user) {
        TiktokVideo video = tiktokVideoRepository.findByIdAndTiktokUpload_User(videoId, user)
                .orElseThrow(() -> new ResourceNotFoundException("TikTok video not found with id: " + videoId));

        return mapToDTO(video);
    }

    private TiktokStatsDTO mapToDTO(TiktokVideo video) {
        return TiktokStatsDTO.builder()
                .id(video.getId())
                .videoId(video.getVideoId())
                .title(video.getTitle())
                .url(video.getUrl())
                .description(video.getDescription())
                .thumbnail(video.getThumbnail())
                .publishedAt(video.getPublishedAt())
                .numOfViews(video.getNumViews())
                .numOfLikes(video.getNumLikes())
                .numOfComments(video.getNumComments())
                .numOfShares(video.getNumShares())
                .build();
    }
}
