package com.suplerteam.video_creator.service.wikipedia;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface WikipediaService {
    String getSectionContent(String pageName, int sectionNumber);
    int getIndexBySectionName(String pageName,String sectionName);
    String getContent(String url);
}
