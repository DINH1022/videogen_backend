package com.suplerteam.video_creator.service.social_connection;

import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.response.AccountSocialConnectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialConnectionStatusServiceImpl implements SocialConnectionStatusService{

    @Autowired
    private UserRepository userRepository;
    @Override
    public AccountSocialConnectionResponse getAccountConnectionStatus(String username) {
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundException("Not found user"));
        String youtubeToken=user.getSocialConnection().getYoutubeToken();
        String tiktokToken=user.getSocialConnection().getTiktokToken();
        return AccountSocialConnectionResponse.builder()
                .youtubeStatus(youtubeToken != null && !youtubeToken.isEmpty())
                .tiktokStatus(tiktokToken!=null && !tiktokToken.isEmpty())
                .build();
    }
}
