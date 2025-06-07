package com.suplerteam.video_creator.request.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiImageApiBody {
    private List<Content> contents;
    private GenerationConfig generationConfig;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationConfig {
        private List<String> responseModalities;
    }

    public static GeminiImageApiBody buildFromTextRequest(TextToImageRequest req) {
        Part part = Part.builder()
                .text(req.getText())
                .build();

        Content content = Content.builder()
                .parts(List.of(part))
                .build();

        GenerationConfig generationConfig = GenerationConfig.builder()
                .responseModalities(List.of("TEXT", "IMAGE"))
                .build();

        return GeminiImageApiBody.builder()
                .contents(List.of(content))
                .generationConfig(generationConfig)
                .build();
    }
}
