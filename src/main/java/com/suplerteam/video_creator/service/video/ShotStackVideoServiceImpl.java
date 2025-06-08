package com.suplerteam.video_creator.service.video;

import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.entity.UserVideo;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.repository.UserVideoRespository;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import com.suplerteam.video_creator.request.video.shot_stack.ShotStackApiBody;
import com.suplerteam.video_creator.response.shot_stack.create.ShotStackSuccessApiResponse;
import com.suplerteam.video_creator.response.shot_stack.status.ShotStackRenderStatusApiResponse;
import com.suplerteam.video_creator.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;

@Service
public class ShotStackVideoServiceImpl implements VideoService{
    @Autowired
    @Qualifier("shotStack-webClient")
    private WebClient.Builder webClientBuilder;

    @Autowired
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserVideoRespository userVideoRepository;

    private Boolean isVideoCreated(ShotStackRenderStatusApiResponse res){
        return res.getSuccess().equals(true)
                && res.getMessage().equalsIgnoreCase("ok")
                && res.getResponse().getStatus().equalsIgnoreCase("done");
    }
    
    @Override
    public String createVideo(CreateVideoRequest req, String username) throws InterruptedException, IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        final int SLEEP_TIME=2000;
        ShotStackApiBody body=ShotStackApiBody.createFromCreateVideoRequest(req);

        ShotStackSuccessApiResponse res=webClientBuilder.build()
                .post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ShotStackSuccessApiResponse.class)
                .block();
        String renderId=res.getResponse().getId();
        Thread.sleep(SLEEP_TIME);
        String url="";
        final int MAX_DURATION_LOOP=2*60*1000;
        long startTime = System.currentTimeMillis();
        while(url.isEmpty() && (System.currentTimeMillis() - startTime) < MAX_DURATION_LOOP){
            ShotStackRenderStatusApiResponse renderResponse=webClientBuilder.build()
                    .get()
                    .uri("/"+renderId)
                    .retrieve()
                    .bodyToMono(ShotStackRenderStatusApiResponse.class)
                    .block();
            if(isVideoCreated(renderResponse)){
                url=renderResponse.getResponse().getUrl();
                break;
            }
            Thread.sleep(SLEEP_TIME);
        }
        url=cloudinaryService.uploadVideoFromUrl(url);
        
        UserVideo userVideo = UserVideo.builder()
                .video_url(url)
                .user(user)
                .title(req.getTitle())
                .createdAt(new Date(Instant.now().toEpochMilli())) // Changed from created_at to createdAt
                .build();
                
        userVideoRepository.save(userVideo);
        
        return url;
    }
}
