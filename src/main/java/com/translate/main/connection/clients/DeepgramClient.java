package com.translate.main.connection.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.translate.main.dto.Transcription;
import com.translate.main.utils.TranslateUtils;
import lombok.extern.slf4j.Slf4j;
import org.jline.utils.Log;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.WebSocketContainer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;


public class DeepgramClient {

    public static final String API_KEY = "c8ac7abc19e6b92160ffbcf4ec54d8a051cdb0cf";

    public static HttpRequest createPostRequest(String uri, HttpRequest.BodyPublisher bodyPublisher) throws FileNotFoundException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "audio/wav")
                .header("Authorization", "Token "+API_KEY)
                .POST(bodyPublisher)
                .build();
        return request;
    }

    public static void getResponse(HttpRequest request) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Transcription transcription = TranslateUtils.mapResponse(response.body());
        TranslateUtils.printResponse(transcription);
    }


    public static void recordingToText(HttpRequest.BodyPublisher bodyPublisher) throws FileNotFoundException {
        HttpRequest httpRequest = DeepgramClient.createPostRequest("https://api.deepgram.com/v1/listen",bodyPublisher);
        try {
            DeepgramClient.getResponse(httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static WebSocket getDeepgramWebsocket(WebSocket.Listener webSocketListener) {
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<WebSocket> ws = client.newWebSocketBuilder()
                .header("Authorization", "Token " + API_KEY)
                .buildAsync(URI.create("wss://api.deepgram.com/v1/listen"), webSocketListener);

        return ws.join();
    }


}
