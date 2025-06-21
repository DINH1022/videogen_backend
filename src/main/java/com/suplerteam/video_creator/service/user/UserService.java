package com.suplerteam.video_creator.service.user;

import com.suplerteam.video_creator.DTO.UserDTO;
import com.suplerteam.video_creator.request.auth.RegisterRequest;

public interface UserService {
    UserDTO register(RegisterRequest req);
    UserDTO getProfile(String username);
}
