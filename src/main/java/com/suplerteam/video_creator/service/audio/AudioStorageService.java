package com.suplerteam.video_creator.service.audio;

import com.suplerteam.video_creator.entity.Audio;
import com.suplerteam.video_creator.repository.AudioRepository;
import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudioStorageService {

    @Autowired
    private AudioRepository audioRepository;

    public Audio saveAudio(String url, TextToSpeechRequest request) {
        Audio audio = Audio.builder()
                .url(url)
                .language(request.getLanguage())
                .voice(request.getVoice())
                .gender(request.getGender())
                .age(request.getAge())
                .build();

        return audioRepository.save(audio);
    }

    public Audio getAudioById(Long id) {
        return audioRepository.findById(id)
                .orElse(null);
    }
}