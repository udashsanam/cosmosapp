package com.cosmos.videochat.controller;

import com.cosmos.videochat.config.SocketHandler;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MyController {

//    private Map<String, WebSocketSession> sessions = SocketHandler.sessions;
//
//    @MessageMapping("/send")
//    public void sendMessage(@Payload String payload,@Headers Map<String, Object> headers) throws IOException, IOException {
//
////        WebSocketSession session = sessions.get(sessionId);
////        System.out.println(sessionId);
////        if (session != null && session.isOpen()) {
////            session.sendMessage(new TextMessage(message));
////        }
//
//        System.out.println("hello word");
//    }
//
////    @EventListener
////    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
////        WebSocketSession session = (WebSocketSession) event.getSource();
////        String sessionId = (String) session.getAttributes().get("sessionId");
////        sessions.put(sessionId, session);
////    }
////
////    @EventListener
////    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
////        WebSocketSession session = (WebSocketSession) event.getSource();
////        String sessionId = (String) session.getAttributes().get("sessionId");
////        sessions.remove(sessionId);
////    }\
//
//    @MessageMapping("/sendMessage")
//    public void sendMessages(@Payload String payload) throws IOException, IOException {
//
//        System.out.println("hello word");
//    }

    @MessageMapping("/SendMessage")
    public void sendMessage(@Payload String payload) {
        // Handle the message here
        System.out.println("message");
    }



}
