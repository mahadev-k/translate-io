package com.translate.main.service.impls;

import com.translate.main.connection.clients.DeepgramClient;
import com.translate.main.connection.clients.DeepgramEndpoint;
import com.translate.main.service.interfaces.AudioRecorder;
import com.translate.main.utils.TranslateUtils;
import org.jline.utils.Log;

import javax.sound.sampled.*;
import java.io.*;
import java.net.http.WebSocket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DeepgramLive extends Thread implements AudioRecorder {

    private File fileToWrite;
    private TargetDataLine targetDataLine;
    private File tempFile;
    private DeepgramEndpoint endpoint;
    private WebSocket webSocket;
    private volatile boolean running = true;

    public DeepgramLive(File fileToWrite, TargetDataLine targetDataLine, File tempFile, DeepgramEndpoint endpoint) {
        this.fileToWrite = fileToWrite;
        this.targetDataLine = targetDataLine;
        this.tempFile = tempFile;
        this.endpoint = endpoint;
    }

    @Override
    public void recordAudio() throws IOException, InterruptedException, LineUnavailableException {
        targetDataLine.open();
        targetDataLine.start();
        Thread thread = new Thread(runnable);
        thread.start();
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime future = LocalDateTime.now().plus(1000, ChronoUnit.MILLIS);
        while(time.isBefore(future)){
            AudioInputStream recordingStream = new AudioInputStream(targetDataLine);
            AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, tempFile);
            TranslateUtils.copyFile(tempFile, fileToWrite);
            time = LocalDateTime.now();
        }
        if(thread.isAlive()){
            thread.join();
        }
        while(!tempFile.canRead()){

        }
        endpoint.sendMessage(new FileInputStream(tempFile), webSocket);
        TranslateUtils.emptyFile(tempFile);
    }

    private Runnable runnable = () ->{
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime future = LocalDateTime.now().plus(1000, ChronoUnit.MILLIS);
        while(time.isBefore(future)){
            time = LocalDateTime.now();
        }
        targetDataLine.stop();
        targetDataLine.close();
    };

    @Override
    public void run() {
        try {
            webSocket = DeepgramClient.getDeepgramWebsocket(endpoint);
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
