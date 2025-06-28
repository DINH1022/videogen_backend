package com.suplerteam.video_creator.service.video;

import com.suplerteam.video_creator.request.video.CreateVideoRequest;

import java.io.IOException;

public interface VideoService {
    String createVideo(CreateVideoRequest req, String workspaceId, String username) throws InterruptedException, IOException;
}
