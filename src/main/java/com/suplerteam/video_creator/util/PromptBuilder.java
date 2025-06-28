package com.suplerteam.video_creator.util;

import com.suplerteam.video_creator.request.text.GenerateTextRequest;

public class PromptBuilder {
    public static String buildPrompt(GenerateTextRequest request) {
        switch (request.getType()) {
            case SHORT_SCRIPT:
                return buildShortScriptPrompt(request);
            case LONG_SCRIPT:
                return buildLongScriptPrompt(request);
            case CAPTION:
                return buildCaptionPrompt(request);
            default:
                return request.getPrompt();
        }
    }
    private static String buildShortScriptPrompt(GenerateTextRequest request) {
        return "Create 1 story (about 3 sentences, only show me content, do not include any quotation marks) about: " + request.getPrompt();
    }

    private static String buildLongScriptPrompt(GenerateTextRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a detailed story that consists of exactly 5 sentences and has a total character count between 380 and 430 characters (including spaces and punctuation), only show me content, do not include any quotation marks) based on this story: \"")
                .append(request.getShortScript())
                .append("\"");

        if (request.getWritingStyle() != null && !request.getWritingStyle().isEmpty()) {
            prompt.append(" in a ").append(request.getWritingStyle()).append(" writing style");
        }

        if (request.getLanguage() != null && !request.getLanguage().isEmpty()) {
            prompt.append(" in ").append(request.getLanguage()).append(" language");
        }

        return prompt.toString();
    }

    private static String buildCaptionPrompt(GenerateTextRequest request) {
        return "Generate 1 engaging social media captions (about 3-4 sentences , only show me content, do not include any quotation marks) for a video with this script: \"" +
                request.getShortScript() + "\"";
    }

    public static String removeQuotationMarks(String content) {
        if (content == null) {
            return null;
        }
        return content.replaceAll("[\"''']", "");
    }
}
