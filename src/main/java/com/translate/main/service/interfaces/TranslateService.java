package com.translate.main.service.interfaces;

import java.io.FileNotFoundException;

public interface TranslateService {

    Integer speechToText(SpeechToText speechToText, long timeInMins);

    void stopTranslation(Integer id);

    void recordingToText(String filePath, RecordingToText recordingToText) throws FileNotFoundException;
}
