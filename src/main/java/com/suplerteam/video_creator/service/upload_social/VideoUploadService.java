package com.suplerteam.video_creator.service.upload_social;

import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;

public interface VideoUploadService {
    Boolean upload(SocialVideoUploadRequest req);
}
