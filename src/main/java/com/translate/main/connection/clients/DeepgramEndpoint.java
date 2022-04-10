package com.translate.main.connection.clients;

import com.translate.main.dto.Transcription;
import com.translate.main.utils.TranslateUtils;
import org.apache.commons.io.IOUtils;
import org.jline.utils.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import javax.websocket.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
public class DeepgramEndpoint implements WSSSendMessage, WebSocket.Listener {


    @Override
    public void onOpen(WebSocket webSocket) {
        Log.info("CONNECTED");
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        Log.info("Recieved binary");
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        Log.info("Recieved ping");
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        Log.info("Recieved pong");
        return WebSocket.Listener.super.onPong(webSocket, message);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        Log.info("Received error");
        WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        Log.info("onText received with data " + data);
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        Log.info("Closed with status " + statusCode + ", reason: " + reason);
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }


    @Override
    public void sendMessage(InputStream inputStream, WebSocket ws) {
        Log.info("Sending messages");
        try{
            byte[] byteArray = IOUtils.toByteArray(inputStream);
            ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
            if(!ws.isOutputClosed()) {
                ws.sendBinary(byteBuffer, false);
            }
        }catch (Exception e){
            Log.info("Error while sending", e);
        }
    }
}
