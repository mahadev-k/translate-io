package com.translate.main.service.impls;

import com.translate.main.connection.clients.DeepgramClient;
import com.translate.main.connection.clients.WSSSendMessage;
import com.translate.main.service.interfaces.AudioRecorder;
import com.translate.main.utils.TranslateUtils;
import lombok.extern.slf4j.Slf4j;
import org.jline.utils.Log;

import javax.sound.sampled.*;
import java.io.*;
import java.net.http.WebSocket;

@Slf4j
public class AudioRecoderImpl extends Thread implements AudioRecorder {

    private File fileToWrite;
    private TargetDataLine targetDataLine;
    private File tempFile;
    private volatile boolean running = true;

    public AudioRecoderImpl(File fileToWrite, TargetDataLine targetDataLine, File tempFile) {
        this.fileToWrite = fileToWrite;
        this.targetDataLine = targetDataLine;
        this.tempFile = tempFile;
    }

    @Override
    public void recordAudio() throws IOException, InterruptedException {
        AudioInputStream recordingStream = new AudioInputStream(targetDataLine);
        AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, tempFile);
        TranslateUtils.copyFile(tempFile, fileToWrite);
    }

    @Override
    public void run() {
        try {
            targetDataLine.open();
            targetDataLine.start();
            while(running){
                recordAudio();
            }
            Log.info("Stopped recording");
        } catch (Exception e) {
            Log.error("Error occurred on thread listen audio ", e);
        }
    }

    public void stopThread(){
        this.targetDataLine.stop();
        this.targetDataLine.close();
        running = false;
        interrupt();
    }
}
