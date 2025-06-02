package com.suplerteam.video_creator.request.video.shot_stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShotStackTimeLine {
    public static final String DEFAULT_BACK_GROUND="#000000";

    @JsonProperty("soundtrack")
    private ShotStackSoundtrack soundtrack;
    @JsonProperty("background")
    private String background;
    @JsonProperty("tracks")
    private List<ShotStackTrack> tracks;

    public static ShotStackTimeLine buildFromCreateVideoRequest(CreateVideoRequest req){
        ShotStackSoundtrack soundtrack1=ShotStackSoundtrack.buildFromCreateVideoRequest(req);
        List<ShotStackTrack> tracks1=ShotStackTrack.buildFromCreateVideoRequest(req);
        return ShotStackTimeLine.builder()
                .soundtrack(soundtrack1)
                .background(DEFAULT_BACK_GROUND)
                .tracks(tracks1)
                .build();
    }
}
