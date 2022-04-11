package com.translate.main.service.interfaces;

import java.io.FileNotFoundException;

public interface TranslateService {

    Integer liveSpeechToText(LiveTranscription liveTranscription);

    Integer speechToText(SpeechToText speechToText, long timeInMins, long timeInSeconds, String language);

    void stopTranslation(Integer id);

    void recordingToText(String filePath, RecordingToText recordingToText, String language) throws FileNotFoundException;

    void stopLiveTranslationThread(Integer id);
}
