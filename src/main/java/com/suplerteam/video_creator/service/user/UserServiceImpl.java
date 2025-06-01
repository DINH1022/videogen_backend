package com.suplerteam.video_creator.service.user;

import com.suplerteam.video_creator.DTO.UserDTO;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.exception.CustomBadRequestException;
import com.suplerteam.video_creator.exception.DuplicateResourceException;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.request.auth.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService{
    private final String DEFAULT_ROLE_STRING="ROLE_CUSTOMER";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Boolean isPasswordConfirmed(RegisterRequest req){
        return req.getPassword().equals(req.getConfirmPassword());
    }
    @Override
    public UserDTO register(RegisterRequest req) {
        if(userRepository.existsByUsername(req.getUsername())){
            throw new DuplicateResourceException("Username is already used");
        }
        if(!isPasswordConfirmed(req)){
            throw new CustomBadRequestException("Please give correct confirm password");
        }
        String hashedPassword=passwordEncoder.encode(req.getPassword());
        User user=User.builder()
                .fullName(req.getFullName())
                .username(req.getUsername())
                .password(hashedPassword)
                .email("")
                .avatar("")
                .role(DEFAULT_ROLE_STRING)
                .createdAt(new Date())
                .build();
        var saved=userRepository.save(user);
        return UserDTO.createFromEntity(user);
    }

}
