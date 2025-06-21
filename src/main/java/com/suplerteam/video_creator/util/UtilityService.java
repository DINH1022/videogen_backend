package com.suplerteam.video_creator.util;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilityService {
    public String getUUID(){
        return UUID.randomUUID().toString();
    }
}
