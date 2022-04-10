package com.translate.main.connection.clients;

import javax.websocket.ClientEndpointConfig;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DeepgramConfig extends ClientEndpointConfig.Configurator {


    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        headers.put("Authorization", Arrays.asList("Token "+DeepgramClient.API_KEY));
    }
}
