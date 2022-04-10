package com.translate.main.service.interfaces;

import java.io.IOException;

public interface SpeechToText {

    void convertSpeechToText() throws IOException;

    void stopThread();

}
