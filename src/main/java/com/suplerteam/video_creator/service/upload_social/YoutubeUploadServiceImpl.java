package com.suplerteam.video_creator.service.upload_social;

import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.entity.YoutubeUploads;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.request.social_video_upload.SocialVideoUploadRequest;
import com.suplerteam.video_creator.service.upload_to_social_client.VideoUploaderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Qualifier("YoutubeUploadServiceImpl")
public class YoutubeUploadServiceImpl implements VideoUploadService{

    private final String YOUTUBE_BASE_URL="https://www.youtube.com/watch";
    @Autowired
    @Qualifier("youtube-uploader-Service")
    private VideoUploaderClient videoUploaderClient;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public String upload(SocialVideoUploadRequest req) {
        User user=userRepository.findByUsername(req.getUsername())
                .orElseThrow(()->new ResourceNotFoundException("Not found user"));
        String videoId=videoUploaderClient.uploadVideo(req);
        YoutubeUploads newUpload=YoutubeUploads
                .builder()
                .user(user)
                .videoId(videoId)
                .build();
        user.getYoutubeUploads().add(newUpload);
        return String.format("%s?v=%s",YOUTUBE_BASE_URL,videoId);
    }
}
