package com.suplerteam.video_creator.service.audio;

import com.suplerteam.video_creator.request.audio.CambAI.CambAIApiBody;
import com.suplerteam.video_creator.request.audio.TextToSpeechRequest;
import com.suplerteam.video_creator.response.CambAI.CambAICreateApiResponse;
import com.suplerteam.video_creator.response.CambAI.CambAIErrorApiResponse;
import com.suplerteam.video_creator.response.CambAI.CambAITaskStatusApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;

@Service
@Qualifier("cambAI-AudioService")
public class CambAIAudioServiceImpl implements AudioService{

    @Autowired
    @Qualifier("cambAI-webClient")
    private WebClient.Builder webClientBuilder;

    private final String RELATIVE_URI_CREATE_TASK="/tts";
    private final String RELATIVE_URI_CHECK_TASK_STATUS="/tts";
    private final String RELATIVE_URI_GET_AUDIO_FILE="/tts-result";


    @Override
    public InputStreamResource textToSpeech(TextToSpeechRequest req) {
        CambAIApiBody cambAIApiBody=CambAIApiBody.builder()
                .text(req.getText())
                .voiceId(req.getVoice()!=null?req.getVoice():CambAIApiBody.DEFAULT_VOICE_ID)
                .language(req.getLanguage()!=null?req.getVoice():CambAIApiBody.DEFAULT_LANGUAGE)
                .gender(req.getGender()!=null?req.getVoice():CambAIApiBody.DEFAULT_GENDER)
                .age(req.getAge()!=null?req.getVoice():CambAIApiBody.DEFAULT_AGE)
                .build();

        CambAICreateApiResponse createResponse = webClientBuilder.build()
                .post()
                .uri(RELATIVE_URI_CREATE_TASK)
                .bodyValue(cambAIApiBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(CambAIErrorApiResponse.class)
                                .map(error -> new RuntimeException("TTS request failed: " + error.getPayload()))
                )
                .bodyToMono(CambAICreateApiResponse.class)
                .block();
        String taskId = createResponse.getTaskId();
        String runId = null;
        while (true) {
            final String COMPLETED_STATUS="SUCCESS";
            CambAITaskStatusApiResponse statusResponse = webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .path(RELATIVE_URI_CHECK_TASK_STATUS+"/"+taskId)
                            .build())
                    .retrieve()
                    .bodyToMono(CambAITaskStatusApiResponse.class)
                    .block();
            if (COMPLETED_STATUS.equalsIgnoreCase(statusResponse.getStatus())) {
                runId = statusResponse.getRunId();
                break;
            }
            else if (statusResponse.getExceptionReason()!=null) {
                throw new RuntimeException("TTS task failed: " + statusResponse.getExceptionReason());
            }
            try {
                Thread.sleep(3000); //sleep 3seconds before check again
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            }
        }
        byte[] audioBytes = webClientBuilder.build()
                .get()
                .uri(RELATIVE_URI_GET_AUDIO_FILE+"/"+runId)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(audioBytes);
        return new InputStreamResource(byteArrayInputStream);
    }
}
