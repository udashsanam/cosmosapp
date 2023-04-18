package com.cosmos.videochat.config;

import org.modelmapper.internal.util.CopyOnWriteLinkedHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {
    public static final Map<String, WebSocketSession> sessions = new CopyOnWriteLinkedHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put("sanam", session);

            System.out.println("hello");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        for (WebSocketSession webSocketSession : sessions.values().toArray(new WebSocketSession[0])) {
//            if (webSocketSession.isOpen() && !webSocketSession.getId().equals(session.getId())) {
//                webSocketSession.sendMessage(message);
//            }
//        }

        System.out.println("hello");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }


}
