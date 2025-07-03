package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.response.wikipedia.WikiRelatingResponse;
import com.suplerteam.video_creator.service.wikipedia.WikipediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wiki")
public class WikipediaController {

    @Autowired
    private WikipediaService wikipediaService;

    @GetMapping("/search")
    public ResponseEntity<List<WikiRelatingResponse>> searchRelatingPosts(
            @RequestParam(name = "search-term")String searchTerm){
        return ResponseEntity.ok(wikipediaService.getRelatingLinks(searchTerm));
    }

    @GetMapping("/get-content")
    public ResponseEntity<String> getContentFromUrl(
            @RequestParam(name = "url")String url){
        System.out.println(url);
        return ResponseEntity.ok(wikipediaService.getContent(url));
    }
}

