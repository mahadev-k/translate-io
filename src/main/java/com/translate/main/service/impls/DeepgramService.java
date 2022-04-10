package com.translate.main.service.impls;

import com.translate.main.connection.clients.DeepgramClient;
import com.translate.main.connection.clients.DeepgramEndpoint;
import com.translate.main.connection.clients.WSSSendMessage;
import com.translate.main.dto.Transcription;
import com.translate.main.service.interfaces.RecordingToText;
import com.translate.main.service.interfaces.SpeechToText;
import com.translate.main.utils.TranslateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.WebSocket;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Slf4j
public class DeepgramService implements SpeechToText, RecordingToText {

    private volatile boolean running = true;
    private AudioRecoderImpl audioRecorder;


    @Override
    public void recordingToText(String fileName) throws FileNotFoundException {
        DeepgramClient.recordingToText(HttpRequest.BodyPublishers
                .ofFile(Paths.get(fileName)));
        //TranslateUtils.printResponse(transcription);
    }


    @Override
    public void convertSpeechToText() throws IOException {
        Optional<TargetDataLine> targetDataLine= TranslateUtils.getTargetDataLine();
        File fileToWrite = TranslateUtils.createRecordingFileWithExtension(".wav");
        File tempFile = TranslateUtils.createRecordingFileWithName("record.wav");
        if (targetDataLine.isPresent()) {
            AudioRecoderImpl audioRecorder = new AudioRecoderImpl(fileToWrite, targetDataLine.get(), tempFile);
            audioRecorder.start();
            this.audioRecorder = audioRecorder;
        }
    }

    @Override
    public void stopThread(){
        this.audioRecorder.stopThread();
    }

    public boolean threadAlive(){
        return audioRecorder.isAlive();
    }

}
