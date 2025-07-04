package com.suplerteam.video_creator.service.social_connection;

public interface SocialAccountLinkingService {
    String getAuthURL(Long userId);
    String getRefreshTokenByAuthCode(String authorizationCode);
    Boolean connectToSocialAccount(Long userId,String authorizationCode);
}
