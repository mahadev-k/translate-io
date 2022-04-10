package com.translate.main.connection.clients;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.http.WebSocket;

public interface WSSSendMessage {

    void sendMessage(InputStream inputStream, WebSocket ws);
}
