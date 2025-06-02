package com.suplerteam.video_creator.request.video.shot_stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import com.suplerteam.video_creator.request.video.VideoImageSegment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShotStackTrack {
    @JsonProperty("clips")
    private List<ShotStackClip> clips;

    public static List<ShotStackTrack> buildFromCreateVideoRequest(
            CreateVideoRequest req){
        List<ShotStackClip> imageClips=req.getImageSegments()
                .stream().map(ShotStackClip::buildFromCreateVideoRequest)
                .toList();
        return  List.of(
                    ShotStackTrack
                    .builder()
                    .clips(imageClips)
                    .build());
    }
}
