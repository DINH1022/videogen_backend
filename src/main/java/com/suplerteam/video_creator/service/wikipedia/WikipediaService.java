package com.suplerteam.video_creator.service.wikipedia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.suplerteam.video_creator.response.wikipedia.WikiRelatingResponse;

import java.util.List;

public interface WikipediaService {
    String getSectionContent(String pageName, int sectionNumber);
    int getIndexBySectionName(String pageName,String sectionName);
    String getContent(String url);
    List<WikiRelatingResponse> getRelatingLinks(String topic);
}
