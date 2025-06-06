package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.request.text.GenerateTextRequest;
import com.suplerteam.video_creator.service.text.TextAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/text")
public class TextController {

    @Autowired
    @Qualifier("DeepSeek-TextService")
    private TextAIService textAIService;


    @PostMapping("/generate")
    public ResponseEntity<String> generateText(@RequestBody GenerateTextRequest request)
            throws IOException, InterruptedException {
            String content = textAIService.generateContent(request);
            return ResponseEntity.ok(content);
    }
}