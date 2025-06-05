package com.suplerteam.video_creator.service.upload_to_social_client;

import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;

import java.util.List;

public interface VideoUploaderClient {
    String uploadVideo(SocialVideoUploadRequest req);
}
