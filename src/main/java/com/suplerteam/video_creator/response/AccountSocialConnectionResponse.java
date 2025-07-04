package com.suplerteam.video_creator.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSocialConnectionResponse {
    @JsonProperty("youtube_status")
    private Boolean youtubeStatus;
    @JsonProperty("tiktok_status")
    private Boolean tiktokStatus;
}
