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
            case SOCIAL_MEDIA_TITLE:
                return buildTitlePrompt(request);
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

        if (request.getTopic() != null && !request.getTopic().isEmpty()) {
            prompt.append(" , each sentence must contain the ").append(request.getTopic()).append(" topic");
        }

        return prompt.toString();
    }

    private static String buildCaptionPrompt(GenerateTextRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate 1 engaging social media description (about 4-5 sentences , only show me content, do not include any quotation marks) for a video with this script: \"")
                .append(request.getShortScript())
                .append("\"");

        if (request.getLanguage() != null && !request.getLanguage().isEmpty()) {
            prompt.append(" in ").append(request.getLanguage()).append(" language");
        }
        return prompt.toString();
    }

    private static String buildTitlePrompt(GenerateTextRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate 1 engaging social media title (1 sentence , only show me content, do not include any quotation marks) for a video with this script: \"")
                .append(request.getShortScript())
                .append("\"");

        if (request.getLanguage() != null && !request.getLanguage().isEmpty()) {
            prompt.append(" in ").append(request.getLanguage()).append(" language");
        }
        return prompt.toString();
    }

    public static String removeQuotationMarks(String content) {
        if (content == null) {
            return null;
        }
        return content.replaceAll("[\"''']", "");
    }
}
