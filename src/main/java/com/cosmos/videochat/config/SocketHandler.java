package com.cosmos.videochat.config;

import com.cosmos.videochat.dto.TextMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.internal.util.CopyOnWriteLinkedHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {
    public static final Map<WebSocketSession, String> sessions = new CopyOnWriteLinkedHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.put(session, session.getHandshakeHeaders().get("userid").get(0));
            System.out.println("hello");
            session.sendMessage(new TextMessage("Welcome cosmos"));
//            session.sendMessage(new TextMessage("Websocket connected " + session.getHandshakeHeaders().get("userid").get(0)));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        TextMessageDto textMessageDto = objectMapper.readValue(message.getPayload(), TextMessageDto.class);
        System.out.println("hello");
        for (Map.Entry<WebSocketSession, String> entry:
        sessions.entrySet()) {
            if(entry.getValue().equals(textMessageDto.getUserid()))
                entry.getKey().sendMessage(new TextMessage(textMessageDto.getData()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void sendMessageToUser(String username, String message) throws IOException {
//        // Get the user's session from the map or data structure
//        WebSocketSession session = userSessions.get(username);
//
//        // Send the message to the user's session
//        session.sendMessage(new TextMessage(message));
    }


}
