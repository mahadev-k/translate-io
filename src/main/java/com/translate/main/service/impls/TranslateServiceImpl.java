package com.translate.main.service.impls;

import com.translate.main.connection.clients.DeepgramClient;
import com.translate.main.service.interfaces.RecordingToText;
import com.translate.main.service.interfaces.SpeechToText;
import com.translate.main.service.interfaces.TranslateService;
import org.jline.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TranslateServiceImpl implements TranslateService {

    Map<Integer, SpeechToText> threadMap = new HashMap<>();
    Integer threadId = 0;

    @Override
    public Integer speechToText(SpeechToText speechToText, long timeInMins) {
        try {
            speechToText.convertSpeechToText();
            new Thread(stopThread(speechToText, timeInMins)).start();
        } catch (IOException e) {
            Log.error("Error IO",e);
        }
        threadMap.put(threadId, speechToText);
        return threadId++;
    }

    private static Runnable stopThread(SpeechToText speechToText, long timeInMins){
        LocalDateTime future = LocalDateTime.now().plusMinutes(timeInMins);
        return () -> {
            while(true) {
                if (LocalDateTime.now().isAfter(future)) {
                    ((DeepgramService) speechToText).stopThread();
                    if(!((DeepgramService)speechToText).threadAlive()) {
                        try {
                            ((DeepgramService) speechToText).recordingToText("recordings/record.wav");
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
    public void recordingToText(String filePath, RecordingToText recordingToText) throws FileNotFoundException {
        recordingToText.recordingToText(filePath);
    }
}
