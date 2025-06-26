package com.suplerteam.video_creator.service.social_video_insights;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.DTO.TiktokStatsDTO;
import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.entity.SocialAccountConnection;
import com.suplerteam.video_creator.entity.TiktokUploads;
import com.suplerteam.video_creator.entity.YoutubeUploads;
import com.suplerteam.video_creator.repository.SocialAccountConnectionRepository;
import com.suplerteam.video_creator.repository.TiktokVideosRepository;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SocialVideoInsightsServiceImpl implements SocialVideoInsightsService{
    @Value("${myapp.parameters.youtube-stats-url}")
    private String YOUTUBE_STATS_URL;
    @Value("${myapp.parameters.google-api-key}")
    private String GOOGLE_API_KEY;
    private String YOUTUBE_PREFIX_URL="https://www.youtube.com/watch?v=";
    private String TIKTOK_PREFIX_URL="https://www.tiktok.com/@username/video/";

    private final String APPLICATION_TIME_ZONE="Asia/Ho_Chi_Minh";

    @Autowired
    private YoutubeVideosRepository youtubeVideosRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TiktokVideosRepository tiktokVideosRepository;
    @Autowired
    private SocialAccountConnectionRepository socialAccountConnectionRepository;

    private WebClient.Builder getYoutubeWebClientBuilder(){
        return WebClient.builder()
                .baseUrl(YOUTUBE_STATS_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    private WebClient.Builder getTiktokWebClientBuilder() {
        return WebClient.builder()
                .baseUrl("https://open.tiktokapis.com/v2/video/list/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter((request, next) -> {
                    System.out.println("Request URL: " + request.url());
                    System.out.println("Request Headers: " + request.headers());
                    return next.exchange(request)
                            .doOnNext(clientResponse -> {
                                System.out.println("Response Status: " + clientResponse.statusCode());
                            });
                });
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


    @Override
    public Long getTotalViewOfUploadedVideosOnTiktok(String username) {
        int SIZE = 100000;
        UserVideosStatsRequest req = UserVideosStatsRequest.builder()
                .username(username)
                .page(0)
                .size(SIZE)
                .build();
        List<TiktokStatsDTO> allVideosStats = this.getStatsOfTiktokVideosOfUser(req);
        return allVideosStats.stream()
                .mapToLong(TiktokStatsDTO::getNumOfViews)
                .sum();
    }

    @Override
    public List<TiktokStatsDTO> getStatsOfTiktokVideosOfUser(UserVideosStatsRequest req) {
        try {
            Pageable pageable = PageRequest.of(req.getPage(), req.getSize());
            List<TiktokUploads> tiktokUploads = tiktokVideosRepository
                    .findByUser_Username(req.getUsername(), pageable)
                    .getContent();

            // Return empty list if no uploads found
            if (tiktokUploads.isEmpty()) {
                return new ArrayList<>();
            }

            // Get the user's TikTok access token
            SocialAccountConnection socialConnection = socialAccountConnectionRepository
                    .findByUser_Username(req.getUsername())
                    .orElseThrow(() -> new RuntimeException("Social connection not found for user"));

            String tiktokToken = socialConnection.getTiktokToken();
            if (tiktokToken == null || tiktokToken.isEmpty()) {
                throw new RuntimeException("TikTok token not found for user");
            }

            // Extract video IDs from the uploads
            List<String> videoIds = tiktokUploads.stream()
                    .map(TiktokUploads::getVideoId)
                    .toList();

            // Create request body according to TikTok API specs
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("video_ids", videoIds);

            // Fields as comma-separated string (not as array)
            requestBody.put("fields", "id,title,cover_image_url,share_url,video_description,like_count,comment_count,share_count,view_count,create_time");

            System.out.println("TikTok request body: " + new ObjectMapper().writeValueAsString(requestBody));
            System.out.println("TikTok token (first 10 chars): " + (tiktokToken.length() > 10 ? tiktokToken.substring(0, 10) + "..." : tiktokToken));

            // Make the API call with detailed logging
            String response = WebClient.builder()
                    .baseUrl("https://open.tiktokapis.com/v2")
                    .filter((request, next) -> {
                        System.out.println("Request URL: " + request.url());
                        System.out.println("Request Headers: " + request.headers());
                        try {
                            System.out.println("Request Body: " + new ObjectMapper().writeValueAsString(requestBody));
                        } catch (Exception e) {
                            System.out.println("Could not print request body");
                        }
                        return next.exchange(request)
                                .doOnNext(clientResponse -> {
                                    System.out.println("Response Status: " + clientResponse.statusCode());
                                });
                    })
                    .build()
                    .post()
                    .uri("/video/query/")
                    .header("Authorization", "Bearer " + tiktokToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("TikTok raw response: " + response);

            JsonNode jsonNode = objectMapper.readTree(response);

            // Better error handling
            if (jsonNode.has("error")) {
                JsonNode errorNode = jsonNode.get("error");
                String errorCode = errorNode.path("code").asText();
                String errorMessage = errorNode.path("message").asText();
                String logId = errorNode.path("log_id").asText();

                if (!"ok".equals(errorCode)) {
                    throw new RuntimeException("TikTok API error: " + errorCode + " - " + errorMessage + " (log_id: " + logId + ")");
                }
            }

            // Process the videos data
            JsonNode videosNode = jsonNode.path("data").path("videos");
            List<TiktokStatsDTO> result = new ArrayList<>();

            for (JsonNode videoNode : videosNode) {
                String videoId = videoNode.path("id").asText();
                TiktokStatsDTO statsDTO = TiktokStatsDTO.builder()
                        .videoId(videoId)
                        .title(videoNode.path("title").asText())
                        .url(videoNode.path("share_url").asText())
                        .thumbnail(videoNode.path("cover_image_url").asText())
                        .publishedAt(LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(videoNode.path("create_time").asLong()),
                                ZoneId.of(APPLICATION_TIME_ZONE)))
                        .numOfViews(videoNode.path("view_count").asLong())
                        .numOfLikes(videoNode.path("like_count").asInt())
                        .numOfComments(videoNode.path("comment_count").asInt())
                        .numOfShares(videoNode.path("share_count").asInt())
                        .build();
                result.add(statsDTO);
            }

            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse TikTok API response: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get TikTok video stats: " + e.getMessage(), e);
        }
    }



}
