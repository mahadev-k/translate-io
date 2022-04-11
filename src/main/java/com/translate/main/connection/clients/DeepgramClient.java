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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class DeepgramClient {

    public static final String API_KEY = "f02b00f59764ce45638c576292c192d119db104f";

    public static HttpRequest createPostRequest(String uri, HttpRequest.BodyPublisher bodyPublisher) throws FileNotFoundException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "audio/wav")
                .header("Authorization", "Token "+API_KEY)
                .POST(bodyPublisher)
                .build();
        return request;
    }

    public static void getResponse(HttpRequest request) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(300))
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(TranslateUtils::mapResponse)
                .thenAccept(t -> {
                    try {
                        TranslateUtils.printResponse(t);
                    } catch (IOException e) {
                        Log.error("IO exc", e);
                    }
                }).get(300, TimeUnit.SECONDS);
    }


    public static void recordingToText(HttpRequest.BodyPublisher bodyPublisher, String language) throws FileNotFoundException {
        HttpRequest httpRequest = DeepgramClient.createPostRequest("https://api.deepgram.com/v1/listen"+"?language="+language,bodyPublisher);
        try {
            DeepgramClient.getResponse(httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
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
