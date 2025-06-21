package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.DTO.UserDTO;
import com.suplerteam.video_creator.response.user.UserResponse;
import com.suplerteam.video_creator.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        UserDTO dto=userService.getProfile(username);
        return ResponseEntity.ok(UserResponse.createFromDTO(dto));
    }
}
