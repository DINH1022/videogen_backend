package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.response.token.TokenInformation;
import com.suplerteam.video_creator.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/token")
@RestController
public class TokenController {

    @Autowired
    private JWTService jwtService;
    @GetMapping("/information")
    public ResponseEntity<TokenInformation> getTokenInformation(
            @RequestParam(name = "token")String token){
        return ResponseEntity.ok(jwtService.getTokenInformation(token));
    }
}
