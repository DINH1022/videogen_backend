package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.DTO.UserDTO;
import com.suplerteam.video_creator.exception.ResourceNotFoundException;
import com.suplerteam.video_creator.request.auth.LoginRequest;
import com.suplerteam.video_creator.request.auth.RegisterRequest;
import com.suplerteam.video_creator.service.JWTService;
import com.suplerteam.video_creator.service.user.UserService;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest registerRequest){
        userService.register(registerRequest);
        return ResponseEntity.ok("Register successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest){
        Authentication authentication=authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        UserDetails userDetails=userDetailsService.loadUserByUsername(authentication.getName());
        
        // Get additional user info to include in token
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDTO userDTO = UserDTO.createFromEntity(user);

        
        // Create claims with user ID and any other necessary data
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        
        String jwtToken = jwtService.generateToken(extraClaims, userDetails);
        Map<String,Object> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("user", userDTO);

        return ResponseEntity.ok(response);
    }
}
