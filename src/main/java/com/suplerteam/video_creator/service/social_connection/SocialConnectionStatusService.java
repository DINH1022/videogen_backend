package com.suplerteam.video_creator.service.social_connection;

import com.suplerteam.video_creator.response.AccountSocialConnectionResponse;

public interface SocialConnectionStatusService {
    AccountSocialConnectionResponse getAccountConnectionStatus(String username);
}
