package com.suplerteam.video_creator.service.social_video_insights;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.entity.YoutubeUploads;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.repository.YoutubeVideosRepository;
import com.suplerteam.video_creator.request.social_video_stats.UserVideosStatsRequest;
import com.suplerteam.video_creator.response.youtube.ApiCall.YoutubeStatsApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class SocialVideoInsightsServiceImpl implements SocialVideoInsightsService{
    @Value("${myapp.parameters.youtube-stats-url}")
    private String YOUTUBE_STATS_URL;
    @Value("${myapp.parameters.google-api-key}")
    private String GOOGLE_API_KEY;
    private String YOUTUBE_PREFIX_URL="https://www.youtube.com/watch?v=";

    private final String APPLICATION_TIME_ZONE="Asia/Ho_Chi_Minh";

    @Autowired
    private YoutubeVideosRepository youtubeVideosRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private WebClient.Builder getYoutubeWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(YOUTUBE_STATS_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
    @Override
    public List<YoutubeStatsDTO> getStatsOfYoutubeVideosOfUser(UserVideosStatsRequest req) {
        try{
//            User user=userRepository.findByUsername(req.getUsername())
//                    .orElseThrow(()->new ResourceNotFoundException("Not found user"));
            Pageable pageable = PageRequest.of(req.getPage(), req.getSize());
            List<String> videoIds=youtubeVideosRepository
                    .findByUser_Username(req.getUsername(),pageable)
                    .getContent()
                    .stream()
                    .map(YoutubeUploads::getVideoId)
                    .toList();
            String videoIdParams=String.join(",",videoIds);
            String response = getYoutubeWebClientBuilder().build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("part","snippet,statistics")
                            .queryParam("id",videoIdParams)
                            .queryParam("key",GOOGLE_API_KEY)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode jsonNode=objectMapper.readTree(response);
            List<YoutubeStatsApiResponse> youtubeStatsResponses=objectMapper
                    .convertValue(jsonNode.path("items"), new TypeReference<>() {
                    });
            List<YoutubeStatsDTO> res=new ArrayList<>();
            for(int i=0;i<youtubeStatsResponses.size();++i){
                var current=youtubeStatsResponses.get(i);
                var newDTO=YoutubeStatsDTO.builder()
                        .title(current.getSnippet().getTitle())
                        .url(YOUTUBE_PREFIX_URL+videoIds.get(i))
                        .thumbnail(current.getThumbnail())
                        .publishedAt(current.getSnippet()
                                .getPublishedAt()
                                .atZoneSameInstant(ZoneId.of(APPLICATION_TIME_ZONE))
                                .toLocalDateTime())
                        .numOfViews(current.getStatistics().getViewCount())
                        .numOfLikes(current.getStatistics().getLikeCount())
                        .numOfComments(current.getStatistics().getCommentCount())
                        .build();
                res.add(newDTO);
            }
            return res;
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Long getTotalViewOfUploadedVideosOnYoutube(String username) {
        int SIZE=100000;
        UserVideosStatsRequest req=UserVideosStatsRequest.builder()
                .username(username)
                .page(0)
                .size(SIZE)
                .build();
        List<YoutubeStatsDTO> allVideosStats=this.getStatsOfYoutubeVideosOfUser(req);
        return allVideosStats.stream()
                .mapToLong(YoutubeStatsDTO::getNumOfViews)
                .sum();
    }
}
