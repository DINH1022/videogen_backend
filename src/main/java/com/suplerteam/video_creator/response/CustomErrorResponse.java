package com.suplerteam.video_creator.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomErrorResponse {
    private Boolean success;
    private String message;

    public CustomErrorResponse(String msg){
        success=false;
        message=msg;
    }
}
