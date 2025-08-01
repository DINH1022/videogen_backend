package com.suplerteam.video_creator.service.social_video_insights;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.DTO.TiktokAggregateStatsDTO;
import com.suplerteam.video_creator.DTO.TiktokStatsDTO;
import com.suplerteam.video_creator.DTO.YoutubeStatsDTO;
import com.suplerteam.video_creator.entity.*;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.*;
import com.suplerteam.video_creator.request.social_video_stats.UserVideosStatsRequest;
import com.suplerteam.video_creator.response.youtube.ApiCall.YoutubeStatsApiResponse;
import com.suplerteam.video_creator.service.upload_to_social_client.TiktokUploaderClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.*;

@Service
public class SocialVideoInsightsServiceImpl implements SocialVideoInsightsService{
    @Value("${myapp.parameters.youtube-stats-url}")
    private String YOUTUBE_STATS_URL;
    @Value("${myapp.parameters.google-api-key}")
    private String GOOGLE_API_KEY;
    private String YOUTUBE_PREFIX_URL="https://www.youtube.com/watch?v=";
    private String TIKTOK_PREFIX_URL="https://www.tiktok.com/@username/video/";

    private final String APPLICATION_TIME_ZONE="Asia/Ho_Chi_Minh";
    private static final Logger logger = LoggerFactory.getLogger(SocialVideoInsightsServiceImpl.class);
    @Autowired
    private YoutubeVideosRepository youtubeVideosRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TiktokVideoRepository tiktokVideoRepository;

    @Autowired
    private TiktokUploadRepository tiktokUploadRepository;

    @Autowired
    private SocialAccountConnectionRepository socialAccountConnectionRepository;



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


    @Override
    public List<TiktokStatsDTO> getAllTiktokVideos(UserVideosStatsRequest req) {
        logger.info("Fetching TikTok video stats directly from TikTok API for user: {}", req.getUsername());

        try {
            // Get access token for the user
            String accessToken = getTiktokAccessToken(req.getUsername());

            // Create WebClient for TikTok API
            WebClient webClient = WebClient.builder()
                    .defaultHeader("Authorization", "Bearer " + accessToken)
                    .defaultHeader("Content-Type", "application/json; charset=UTF-8")
                    .build();

            // Fetch videos directly from TikTok API
            List<TiktokStatsDTO> allVideos = new ArrayList<>();
            String cursor = null;
            boolean hasMore = true;

            // Calculate how many pages we need to skip based on pagination parameters
            int pageToRetrieve = req.getPage();
            int pageSize = req.getSize();
            int videosToSkip = pageToRetrieve * pageSize;
            int totalFetched = 0;

            while (hasMore) {
                // Create request payload for video list
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("max_count", 20); // Maximum allowed by TikTok API
                if (cursor != null) {
                    requestBody.put("cursor", cursor);
                }

                // Make API call to get video list
                String listResponse = webClient.post()
                        .uri("https://open.tiktokapis.com/v2/video/list/?fields=id,title,cover_image_url,share_url")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                // Parse response
                JsonNode listJsonResponse = objectMapper.readTree(listResponse);

                // Check for errors
                if (!listJsonResponse.path("error").path("code").asText("").equals("ok")) {
                    String errorMessage = listJsonResponse.path("error").path("message").asText("Unknown error");
                    logger.error("Error fetching TikTok video list: {}", errorMessage);
                    throw new RuntimeException("TikTok API error: " + errorMessage);
                }

                // Get videos list
                JsonNode videoListData = listJsonResponse.path("data");
                JsonNode videos = videoListData.path("videos");

                // Get video details and stats for each video
                if (videos.isArray() && videos.size() > 0) {
                    List<String> videoIds = new ArrayList<>();
                    Map<String, JsonNode> videoBasicInfo = new HashMap<>();

                    for (JsonNode videoBasic : videos) {
                        String videoId = videoBasic.path("id").asText();
                        videoIds.add(videoId);
                        videoBasicInfo.put(videoId, videoBasic);
                    }

                    // Get detailed stats for these videos
                    Map<String, Object> statsRequestBody = new HashMap<>();
                    Map<String, Object> filters = new HashMap<>();
                    filters.put("video_ids", videoIds);
                    statsRequestBody.put("filters", filters);

                    String statsResponse = webClient.post()
                            .uri("https://open.tiktokapis.com/v2/video/query/?fields=id,title,video_description,share_url,create_time,view_count,like_count,comment_count,share_count,cover_image_url")
                            .bodyValue(statsRequestBody)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

                    JsonNode statsJsonResponse = objectMapper.readTree(statsResponse);

                    // Check for errors
                    if (!statsJsonResponse.path("error").path("code").asText("").equals("ok")) {
                        String errorMessage = statsJsonResponse.path("error").path("message").asText("Unknown error");
                        logger.error("Error fetching TikTok video stats: {}", errorMessage);
                        throw new RuntimeException("TikTok API error: " + errorMessage);
                    }

                    // Process detailed video stats
                    JsonNode detailedVideos = statsJsonResponse.path("data").path("videos");
                    if (detailedVideos.isArray()) {
                        for (JsonNode video : detailedVideos) {
                            // Skip videos if we're not yet at the requested page
                            if (totalFetched < videosToSkip) {
                                totalFetched++;
                                continue;
                            }

                            // Add video to results if within page size
                            if (allVideos.size() < pageSize) {
                                TiktokStatsDTO statsDTO = TiktokStatsDTO.builder()
                                        .title(video.path("title").asText(""))
                                        .url(video.path("share_url").asText(""))
                                        .thumbnail(video.path("cover_image_url").asText(""))
                                        .publishedAt(LocalDateTime.ofInstant(
                                                Instant.ofEpochSecond(video.path("create_time").asLong(0)),
                                                ZoneId.of(APPLICATION_TIME_ZONE)))
                                        .numOfViews(video.path("view_count").asLong(0))
                                        .numOfLikes(video.path("like_count").asInt(0))
                                        .numOfComments(video.path("comment_count").asInt(0))
                                        .numOfShares(video.path("share_count").asInt(0))
                                        .videoId(video.path("id").asText(""))
                                        .description(video.path("video_description").asText(""))
                                        .build();

                                allVideos.add(statsDTO);
                                totalFetched++;
                            } else {
                                // We've reached our page size, no need to continue processing this batch
                                break;
                            }
                        }
                    }
                }

                // Check if we've reached our page size or if there are no more videos
                if (allVideos.size() >= pageSize || !videoListData.path("has_more").asBoolean(false)) {
                    hasMore = false;
                } else {
                    cursor = videoListData.path("cursor").asText();
                }
            }
            saveOrUpdateTiktokVideos(req.getUsername(), allVideos);
            return allVideos;
        } catch (Exception e) {
            logger.error("Error fetching TikTok video stats", e);
            throw new RuntimeException("Failed to fetch TikTok video stats: " + e.getMessage(), e);
        }
    }


    private void saveOrUpdateTiktokVideos(String username, List<TiktokStatsDTO> videos) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        for (TiktokStatsDTO videoStats : videos) {
            Optional<TiktokVideo> existingVideo = tiktokVideoRepository.findByVideoId(videoStats.getVideoId());

            Optional<TiktokUploads> matchingUpload = Optional.ofNullable(tiktokUploadRepository.findByTitle(videoStats.getTitle()));

            if (existingVideo.isPresent()) {
                TiktokVideo video = existingVideo.get();
                video.setTitle(videoStats.getTitle());
                video.setUrl(videoStats.getUrl());
                video.setDescription(videoStats.getDescription());
                video.setThumbnail(videoStats.getThumbnail());
                video.setPublishedAt(videoStats.getPublishedAt());
                video.setNumViews(videoStats.getNumOfViews());
                video.setNumLikes(videoStats.getNumOfLikes());
                video.setNumComments(videoStats.getNumOfComments());
                video.setNumShares(videoStats.getNumOfShares());
                video.setLastUpdated(LocalDateTime.now());

                // If we found a matching upload and it's not already set
                if (matchingUpload.isPresent() && video.getTiktokUpload() == null) {
                    video.setTiktokUpload(matchingUpload.get());
                }

                tiktokVideoRepository.save(video);
            } else {
                // Create new record
                TiktokVideo video = TiktokVideo.builder()
                    .videoId(videoStats.getVideoId())
                    .title(videoStats.getTitle())
                    .url(videoStats.getUrl())
                    .description(videoStats.getDescription())
                    .thumbnail(videoStats.getThumbnail())
                    .publishedAt(videoStats.getPublishedAt())
                    .numViews(videoStats.getNumOfViews())
                    .numLikes(videoStats.getNumOfLikes())
                    .numComments(videoStats.getNumOfComments())
                    .numShares(videoStats.getNumOfShares())
                    .tiktokUpload(matchingUpload.orElse(null))
                    .build();

                tiktokVideoRepository.save(video);
            }
        }
    }


    private String getTiktokAccessToken(String username) {
        // Use the same token retrieval logic as in TiktokUploaderClientImpl
        return socialAccountConnectionRepository.findByUser_Username(username)
                .map(SocialAccountConnection::getTiktokToken)
                .orElseThrow(() -> new RuntimeException("TikTok token not found for user: " + username));
    }

    @Override
    public Long getTotalViewOfUploadedVideosOnTiktok(String username) {
        try {
            logger.info("Calculating total TikTok views from database for user: {}", username);

            UserVideosStatsRequest request = UserVideosStatsRequest.builder()
                    .username(username)
                    .page(0)
                    .size(100)
                    .build();
            //getAllTiktokVideos(request);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

            Long totalViews = tiktokVideoRepository.findByTiktokUpload_User(user)
                    .stream()
                    .mapToLong(video -> video.getNumViews() != null ? video.getNumViews() : 0L)
                    .sum();

            logger.info("Total TikTok views calculated from database: {}", totalViews);
            return totalViews;
        } catch (Exception e) {
            logger.error("Error calculating total TikTok views", e);
            throw new RuntimeException("Failed to calculate total TikTok views: " + e.getMessage(), e);
        }
    }

    @Override
    public TiktokAggregateStatsDTO getTiktokAggregateStats(String username) {
        try {
            logger.info("Calculating TikTok aggregate stats from database for user: {}", username);

            UserVideosStatsRequest request = UserVideosStatsRequest.builder()
                    .username(username)
                    .page(0)
                    .size(100)
                    .build();
            //getAllTiktokVideos(request);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

            List<TiktokVideo> videos = tiktokVideoRepository.findByTiktokUpload_User(user);

            if (videos.isEmpty()) {
                return TiktokAggregateStatsDTO.builder()
                        .totalVideos(0)
                        .totalViews(0L)
                        .totalLikes(0L)
                        .totalComments(0L)
                        .totalShares(0L)
                        .averageViews(0.0)
                        .averageLikes(0.0)
                        .averageComments(0.0)
                        .averageShares(0.0)
                        .build();
            }

            int totalVideos = videos.size();
            long totalViews = videos.stream()
                    .mapToLong(video -> video.getNumViews() != null ? video.getNumViews() : 0L)
                    .sum();
            long totalLikes = videos.stream()
                    .mapToLong(video -> video.getNumLikes() != null ? video.getNumLikes() : 0)
                    .sum();
            long totalComments = videos.stream()
                    .mapToLong(video -> video.getNumComments() != null ? video.getNumComments() : 0)
                    .sum();
            long totalShares = videos.stream()
                    .mapToLong(video -> video.getNumShares() != null ? video.getNumShares() : 0)
                    .sum();

            double averageViews = (double) totalViews / totalVideos;
            double averageLikes = (double) totalLikes / totalVideos;
            double averageComments = (double) totalComments / totalVideos;
            double averageShares = (double) totalShares / totalVideos;

            logger.info("TikTok aggregate stats calculated from database: {} videos, {} views", totalVideos, totalViews);

            return TiktokAggregateStatsDTO.builder()
                    .totalVideos(totalVideos)
                    .totalViews(totalViews)
                    .totalLikes(totalLikes)
                    .totalComments(totalComments)
                    .totalShares(totalShares)
                    .averageViews(averageViews)
                    .averageLikes(averageLikes)
                    .averageComments(averageComments)
                    .averageShares(averageShares)
                    .build();
        } catch (Exception e) {
            logger.error("Error calculating TikTok aggregate stats", e);
            throw new RuntimeException("Failed to calculate TikTok aggregate statistics: " + e.getMessage(), e);
        }
    }

}
