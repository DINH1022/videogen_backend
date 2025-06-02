package com.suplerteam.video_creator.exception;

public class RateLimitApiCallException extends RuntimeException{
    public RateLimitApiCallException(){
        super("Please wait for a moment, you reach your limit to perform this action");
    }
}
