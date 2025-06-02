package com.suplerteam.video_creator.service.wikipedia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.response.wikipedia.WikiSection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class WikipediaServiceImpl implements WikipediaService{
    private final String WIKIPEDIA_BASE_URL="https://en.wikipedia.org";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;

    private String extractTextFromHtml(String html) {
        Document doc = Jsoup.parse(html);

        doc.select("table").remove();
        doc.select("div.navbox").remove();
        doc.select("div.infobox").remove();
        doc.select("sup").remove();
        doc.select("span.mw-editsection").remove();
        return doc.text();
    }
    @Override
    public String getSectionContent(String pageName, int sectionNumber)  {
        try {
            String url = String.format(
                    WIKIPEDIA_BASE_URL+"/w/api.php?action=parse&page=%s&section=%d&format=json",
                    URLEncoder.encode(pageName, StandardCharsets.UTF_8),
                    sectionNumber
            );
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            String htmlContent = jsonNode.path("parse").path("text").path("*").asText();
            return extractTextFromHtml(htmlContent);
        }
        catch (Exception e){
            throw new RuntimeException("Error fetching sections", e);
        }
    }


    public List<WikiSection> getAllSections(String pageName) {
        try {
            String url = String.format(
                    "https://en.wikipedia.org/w/api.php?action=parse&page=%s&prop=sections&format=json",
                    URLEncoder.encode(pageName, StandardCharsets.UTF_8)
            );
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            List<WikiSection> sections = new ArrayList<>();
            JsonNode sectionsNode = jsonNode.path("parse").path("sections");
            for (JsonNode section : sectionsNode) {
                WikiSection wikiSection=WikiSection
                        .builder()
                        .index(section.path("index").asInt())
                        .title(section.path("line").asText())
                        .level(section.path("level").asInt())
                        .build();
                sections.add(wikiSection);
            }
            return sections;

        }
        catch (Exception e) {
            throw new RuntimeException("Error fetching sections", e);
        }
    }
    @Override
    public int getIndexBySectionName(String pageName, String sectionName) {
        if(sectionName==null || sectionName.isEmpty()){
            return 0;
        }
        List<WikiSection> sections=getAllSections(pageName);
        int index=sections
                .stream().filter(section->section.getTitle().equals(sectionName))
                .mapToInt(WikiSection::getIndex)
                .findFirst()
                .orElse(-1);
        return index==-1?0:index;
    }

    @Override
    public String getContent(String url) {
        String wikiInformationUrl=WIKIPEDIA_BASE_URL+"/wiki/";
        String pageName=url.substring(wikiInformationUrl.length(),'#');
        int index=url.indexOf('#');
        String sectionName="";
        if(index!=-1){
            sectionName=url.substring(index+1);
        }
        int sectionIndex=getIndexBySectionName(pageName,sectionName);
        return getSectionContent(pageName,sectionIndex);
    }
}
