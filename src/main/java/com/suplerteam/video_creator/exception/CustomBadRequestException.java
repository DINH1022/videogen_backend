package com.suplerteam.video_creator.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomBadRequestException extends RuntimeException{
    public CustomBadRequestException(String msg){
        super(msg);
    }
}
