package com.suplerteam.video_creator.response.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInformation {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("user")
    private String user;
    @JsonProperty("iat")
    Date iat;
    @JsonProperty("exp")
    Date exp;
}
