package com.suplerteam.video_creator.request.video.shot_stack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.suplerteam.video_creator.request.video.CreateVideoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShotStackApiBody {
    @JsonProperty("timeline")
    ShotStackTimeLine shotStackTimeLine;
    @JsonProperty("output")
    ShotStackOutput shotStackOutputOutput;

    public static ShotStackApiBody createFromCreateVideoRequest(
            CreateVideoRequest req){
        ShotStackTimeLine timeLine=ShotStackTimeLine.buildFromCreateVideoRequest(req);
        ShotStackOutput output=ShotStackOutput.buildFromCreateVideoRequest(req);
        return  ShotStackApiBody.builder()
                .shotStackTimeLine(timeLine)
                .shotStackOutputOutput(output)
                .build();
    }
}
