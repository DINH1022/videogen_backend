package com.suplerteam.video_creator.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class home {
    @GetMapping
    ResponseEntity<String> home(){
        return ResponseEntity.ok("Hello world!");
    }
}
