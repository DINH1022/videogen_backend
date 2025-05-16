package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.exception.DuplicateResourceException;
import com.suplerteam.video_creator.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.server.ExportException;

@RestController
@RequestMapping("/")
public class home {
    @Autowired
    private UserService userService;
    @GetMapping
    ResponseEntity<String> home(){
        return ResponseEntity.ok("Hello World");
    }
}
