package com.translate.main.service.impls;

import com.translate.main.service.interfaces.LiveTranscription;
import com.translate.main.service.interfaces.RecordingToText;
import com.translate.main.service.interfaces.SpeechToText;
import com.translate.main.service.interfaces.TranslateService;
import org.jline.utils.Log;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TranslateServiceImpl implements TranslateService {

    Map<Integer, SpeechToText> threadMap = new HashMap<>();
    Map<Integer, LiveTranscription> threadMapLive = new HashMap<>();
    Integer threadId = 0;

    @Override
    public void stopLiveTranslationThread(Integer id) {
        if(threadMap.get(id) != null){
            ((DeepgramService) threadMap.get(id)).stopDeepgramLiveThread();
        }
    }

    @Override
    public Integer liveSpeechToText(LiveTranscription liveTranscription) {
        try {
            liveTranscription.convertLiveStream();
        } catch (IOException e) {
            Log.error("Error IO",e);
        }
        threadMapLive.put(threadId, liveTranscription);
        return threadId++;
    }

    @Override
    public Integer speechToText(SpeechToText speechToText, long timeInMins, long timeInSeconds, String language) {
        try {
            speechToText.convertSpeechToText();
            new Thread(stopThread(speechToText, timeInMins, timeInSeconds, language)).start();
        } catch (IOException e) {
            Log.error("Error IO",e);
        }
        threadMap.put(threadId, speechToText);
        return threadId++;
    }

    private static Runnable stopThread(SpeechToText speechToText, long timeInMins, long timeInSeconds, String language){
        LocalDateTime future = LocalDateTime.now().plusMinutes(timeInMins).plusSeconds(timeInSeconds);
        return () -> {
            while(true) {
                if (LocalDateTime.now().isAfter(future)) {
                    ((DeepgramService) speechToText).stopThread();
                    if(!((DeepgramService)speechToText).threadAlive()) {
                        try {
                            ((DeepgramService) speechToText).recordingToText("recordings/record.wav", language);
                        } catch (FileNotFoundException e) {
                            Log.error("File not found", e);
                        }
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            Log.info("sptt succesfully finished after :: "+timeInMins);
        };
    }

    @Override
    public void stopTranslation(Integer id) {
        if(threadMap.get(id) != null){
            ((DeepgramService) threadMap.get(id)).stopThread();
        }
    }

    @Override
    public void recordingToText(String filePath, RecordingToText recordingToText, String language) throws FileNotFoundException {
        recordingToText.recordingToText(filePath, language);
    }
}
