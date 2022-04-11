package com.translate.main.service.interfaces;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public interface AudioRecorder {
    void recordAudio() throws IOException, InterruptedException, LineUnavailableException;
}
