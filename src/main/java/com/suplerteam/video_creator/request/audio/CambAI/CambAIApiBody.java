package com.suplerteam.video_creator.request.audio.CambAI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambAIApiBody {
    public static final String DEFAULT_VOICE_ID="20298";
    public static final String DEFAULT_LANGUAGE="136";
    public static final String DEFAULT_GENDER="0";
    public static final String DEFAULT_AGE="25";

    @JsonProperty("text")
    private String text;
    @JsonProperty("voice_id")
    private String voiceId;
    @JsonProperty("language")
    private String language;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("age")
    private String age;
}
