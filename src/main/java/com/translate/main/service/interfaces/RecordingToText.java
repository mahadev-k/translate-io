package com.translate.main.service.interfaces;

import java.io.FileNotFoundException;

public interface RecordingToText {
    void recordingToText(String fileName, String language) throws FileNotFoundException;
}
