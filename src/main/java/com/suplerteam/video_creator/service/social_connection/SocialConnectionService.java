package com.suplerteam.video_creator.service.social_connection;

public interface SocialConnectionService {
    String getAuthURL(Long userId);
    String getRefreshTokenByAuthCode(String authorizationCode);
    Boolean connectToSocialAccount(Long userId,String authorizationCode);
}
