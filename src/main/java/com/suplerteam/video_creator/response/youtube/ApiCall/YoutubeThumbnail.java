package com.suplerteam.video_creator.response.youtube.ApiCall;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeThumbnail {
    @JsonProperty("default")
    YoutubeThumbnailDetail defaultThumbnail;
    @JsonProperty("standard")
    YoutubeThumbnailDetail standardThumbnail;

    public String getThumbnail(){
        if(this.getStandardThumbnail()==null){
            return "youtube-unavailable";
        }
        if(!this.getStandardThumbnail().getUrl().isEmpty()){
            return this.getStandardThumbnail().getUrl();
        }
        return this.getDefaultThumbnail().getUrl();
    }
}
