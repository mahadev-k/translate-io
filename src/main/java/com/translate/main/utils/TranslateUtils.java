package com.translate.main.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.translate.main.dto.Transcription;
import com.translate.main.dto.TranscriptionAlternative;
import com.translate.main.dto.TranscriptionChannel;
import lombok.extern.slf4j.Slf4j;
import org.jline.utils.Log;
import org.springframework.format.annotation.DateTimeFormat;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
public class TranslateUtils {

    public static Optional<TargetDataLine> getTargetDataLine(){
        try {

            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    44100, 16,
                    2, 4, 44100, false);
            Line.Info dataLine = new DataLine.Info(TargetDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(dataLine)) {
                Log.info("Audio not supported");
            }

            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLine);
            return Optional.of(targetDataLine);
        }catch (Exception e){
            Log.error("Error occurred while creating target dataline", e);
        }
        return Optional.empty();
    }

    public static File createRecordingFileWithName(String fileName) throws IOException {
        File file = new File("recordings"+File.separator+fileName);
        if(file.exists())file.delete();
        File newFile = new File("recordings"+File.separator+fileName);
        newFile.createNewFile();
        return newFile;
    }

    public static File createRecordingFileWithExtension(String fileExtension) throws IOException {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss") ;
        String fileName = "record_" + LocalDateTime.now().format(dateFormat) +fileExtension;
        return createRecordingFileWithName(fileName);
    }

    public static void emptyFile(File file) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.print("");
        printWriter.close();
    }

    public static Transcription mapResponse(String response){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            Transcription transcription = objectMapper.readValue(response, Transcription.class);
            return transcription;
        } catch (JsonProcessingException e) {
            Log.error("Error on parse",e);
        }
        return new Transcription();
    }

    public static void printResponse(Transcription transcription) throws IOException {
        String transcript = String.valueOf(transcription.getResult()
                .getTranscriptionChannels().stream().findFirst().map(TranscriptionChannel::getAlternatives)
                .map(ta -> ta.stream().findFirst().map(TranscriptionAlternative::getTranscript).orElse("No Transcripts"))
                .orElse("No transcripts"));
        File basic = TranslateUtils.createRecordingFileWithName("transcript.txt");
        File spare = TranslateUtils.createRecordingFileWithExtension(".txt");
        writeInFile(basic, transcript);
        writeInFile(spare, transcript);
        Log.info("Trying to open transcript file in notepad if didnt open please check recordings folder in the project dir>>");
        ProcessBuilder pb = new ProcessBuilder("Notepad.exe", basic.getAbsolutePath());
        pb.start();
        Log.info("Notepad should be open now");
    }

    public static void writeInFile(File file, String string){
        try(FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            //convert string to byte array
            byte[] bytes = string.getBytes();
            //write byte array to file
            bos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File fromFile, File toFile) throws IOException {

        InputStream inStream = null;
        OutputStream outStream = null;

        try {

            inStream = new FileInputStream(fromFile);
            outStream = new FileOutputStream(toFile);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
                outStream.flush();
            }

        } finally {
            if (inStream != null)
                inStream.close();

            if (outStream != null)
                outStream.close();
        }

    }

}
