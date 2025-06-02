package com.suplerteam.video_creator.service.video;

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

@Service
public class ShotStackVideoServiceImpl implements VideoService{
    @Autowired
    @Qualifier("shotStack-webClient")
    private WebClient.Builder webClientBuilder;

    @Autowired
    private CloudinaryService cloudinaryService;

    private Boolean isVideoCreated(ShotStackRenderStatusApiResponse res){
        return res.getSuccess().equals(true)
                && res.getMessage().equalsIgnoreCase("ok")
                && res.getResponse().getStatus().equalsIgnoreCase("done");
    }
    @Override
    public String createVideo(CreateVideoRequest req) throws InterruptedException, IOException {
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
        return url;
    }
}
